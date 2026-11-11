package com.jiancai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.config.CacheConfig;
import com.jiancai.entity.CallRecord;
import com.jiancai.entity.ForwardRecord;
import com.jiancai.entity.Inquiry;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.MpUser;
import com.jiancai.entity.Product;
import com.jiancai.entity.ViewRecord;
import com.jiancai.mapper.CallRecordMapper;
import com.jiancai.mapper.ForwardRecordMapper;
import com.jiancai.mapper.InquiryMapper;
import com.jiancai.mapper.MerchantMapper;
import com.jiancai.mapper.MpUserMapper;
import com.jiancai.mapper.ProductMapper;
import com.jiancai.mapper.ViewRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 浏览 / 转发 / 电话联系 统计业务
 */
@Service
public class StatService {

    /** 目标类型：1=产品 2=商户 */
    public static final int TARGET_PRODUCT = 1;
    public static final int TARGET_MERCHANT = 2;

    @Autowired
    private ViewRecordMapper viewRecordMapper;

    @Autowired
    private ForwardRecordMapper forwardRecordMapper;

    @Autowired
    private CallRecordMapper callRecordMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MpUserMapper mpUserMapper;

    @Autowired
    private InquiryMapper inquiryMapper;

    /** 记录浏览（流水 + 计数器同事务，失败整体回滚） */
    @Transactional
    public void recordView(Integer targetType, Long targetId, String openid) {
        if (targetId == null) {
            return;
        }
        ViewRecord record = new ViewRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setOpenid(openid);
        viewRecordMapper.insert(record);
        incr(targetType, targetId, "views");
    }

    /** 记录转发 */
    @Transactional
    public void recordForward(Integer targetType, Long targetId, String openid) {
        if (targetId == null) {
            return;
        }
        ForwardRecord record = new ForwardRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setOpenid(openid);
        forwardRecordMapper.insert(record);
        incr(targetType, targetId, "forwards");
    }

    /**
     * 记录电话联系：产品页拨号、比价列表询价、商户主页拨号均走这里
     * merchantId 为空时自动推导
     */
    @Transactional
    public void recordCall(Integer targetType, Long targetId, Long merchantId, String openid) {
        if (targetId == null) {
            return;
        }
        Long ownerId = merchantId;
        if (ownerId == null) {
            if (Integer.valueOf(TARGET_MERCHANT).equals(targetType)) {
                ownerId = targetId;
            } else {
                Product product = productMapper.selectById(targetId);
                ownerId = product == null ? null : product.getMerchantId();
            }
        }
        if (ownerId == null) {
            return;
        }

        CallRecord record = new CallRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setMerchantId(ownerId);
        record.setOpenid(openid);
        callRecordMapper.insert(record);
        incr(targetType, targetId, "calls");
    }

    private void incr(Integer targetType, Long targetId, String column) {
        if (Integer.valueOf(TARGET_PRODUCT).equals(targetType)) {
            productMapper.update(null, Wrappers.<Product>lambdaUpdate()
                    .eq(Product::getId, targetId)
                    .setSql(column + " = " + column + " + 1"));
        } else {
            merchantMapper.update(null, Wrappers.<Merchant>lambdaUpdate()
                    .eq(Merchant::getId, targetId)
                    .setSql(column + " = " + column + " + 1"));
        }
    }

    /** 平台总览数据 */
    public Map<String, Object> overview() {
        Map<String, Object> map = new HashMap<>(16);
        map.put("merchantTotal", merchantMapper.selectCount(null));
        map.put("merchantPending", merchantMapper.selectCount(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getStatus, 0)));
        map.put("userTotal", mpUserMapper.selectCount(null));
        map.put("userToday", mpUserMapper.selectCount(Wrappers.<MpUser>lambdaQuery()
                .ge(MpUser::getCreateTime, LocalDate.now().atStartOfDay())));
        map.put("productTotal", productMapper.selectCount(null));
        map.put("productPending", productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                .eq(Product::getStatus, 0)));
        map.put("viewTotal", viewRecordMapper.selectCount(null));
        map.put("forwardTotal", forwardRecordMapper.selectCount(null));
        map.put("callTotal", callRecordMapper.selectCount(null));
        return map;
    }

    /** 商户端数据概览（60 秒短缓存，看板页高频刷新不再每次 10+ 查询） */
    @Cacheable(cacheNames = CacheConfig.MERCHANT_STAT, key = "#merchantId")
    public Map<String, Object> merchantOverview(Long merchantId) {
        // 一条 GROUP BY 拿回各状态产品数，替代 3 次 selectCount
        Map<Integer, Long> statusCounts = new HashMap<>();
        long productTotal = 0;
        for (Map<String, Object> row : productMapper.selectMaps(new QueryWrapper<Product>()
                .select("status", "COUNT(*) AS c")
                .eq("merchant_id", merchantId)
                .groupBy("status"))) {
            Object status = firstValue(row, "status", "STATUS");
            Number count = (Number) firstValue(row, "c", "C");
            if (count == null) {
                continue;
            }
            long c = count.longValue();
            productTotal += c;
            if (status != null) {
                statusCounts.put(Integer.valueOf(status.toString()), c);
            }
        }
        List<Long> productIds = ownProductIds(merchantId);

        Map<String, Object> map = new HashMap<>(16);
        map.put("productTotal", productTotal);
        map.put("productOnSale", statusCounts.getOrDefault(1, 0L));
        map.put("productPending", statusCounts.getOrDefault(0, 0L));
        map.put("viewTotal", countView(merchantId, productIds));
        map.put("forwardTotal", countForward(merchantId, productIds));
        map.put("callTotal", callRecordMapper.selectCount(Wrappers.<CallRecord>lambdaQuery()
                .eq(CallRecord::getMerchantId, merchantId)));

        Merchant merchant = merchantMapper.selectById(merchantId);
        map.put("merchantViews", merchant == null ? 0 : merchant.getViews());
        map.put("merchantCalls", merchant == null ? 0 : merchant.getCalls());
        map.put("expireTime", merchant == null ? null : merchant.getExpireTime());
        return map;
    }

    /**
     * 商户最近动态：浏览 / 转发 / 电话联系 合并后按时间倒序
     * 每张流水表只取前 limit 条再合并（各表 Top-N 的并集必含全局 Top-N），
     * 避免把商户全部流水拉进内存
     */
    @Cacheable(cacheNames = CacheConfig.MERCHANT_STAT, key = "#merchantId + ':' + #limit")
    public List<Map<String, Object>> merchantRecords(Long merchantId, int limit) {
        int n = Math.max(1, Math.min(limit, 50));
        Map<Long, String> productNames = productMapper
                .selectList(Wrappers.<Product>lambdaQuery().eq(Product::getMerchantId, merchantId))
                .stream()
                .collect(Collectors.toMap(Product::getId,
                        p -> p.getName() == null ? "未命名产品" : p.getName(), (a, b) -> a));
        List<Long> productIds = productNames.isEmpty()
                ? Collections.singletonList(-1L)
                : new ArrayList<>(productNames.keySet());
        Merchant merchant = merchantMapper.selectById(merchantId);
        String merchantName = merchant == null ? "门店" : merchant.getName();

        List<Map<String, Object>> result = new ArrayList<>();

        var viewWrapper = Wrappers.<ViewRecord>lambdaQuery()
                .and(w -> w.eq(ViewRecord::getTargetType, TARGET_MERCHANT)
                        .eq(ViewRecord::getTargetId, merchantId)
                        .or().eq(ViewRecord::getTargetType, TARGET_PRODUCT)
                        .in(ViewRecord::getTargetId, productIds))
                .orderByDesc(ViewRecord::getCreateTime)
                .last("limit " + n);
        for (ViewRecord r : viewRecordMapper.selectList(viewWrapper)) {
            result.add(toRecord("浏览", r.getTargetType(), r.getTargetId(),
                    r.getOpenid(), r.getCreateTime(), productNames, merchantName));
        }

        var forwardWrapper = Wrappers.<ForwardRecord>lambdaQuery()
                .and(w -> w.eq(ForwardRecord::getTargetType, TARGET_MERCHANT)
                        .eq(ForwardRecord::getTargetId, merchantId)
                        .or().eq(ForwardRecord::getTargetType, TARGET_PRODUCT)
                        .in(ForwardRecord::getTargetId, productIds))
                .orderByDesc(ForwardRecord::getCreateTime)
                .last("limit " + n);
        for (ForwardRecord r : forwardRecordMapper.selectList(forwardWrapper)) {
            result.add(toRecord("转发", r.getTargetType(), r.getTargetId(),
                    r.getOpenid(), r.getCreateTime(), productNames, merchantName));
        }

        for (CallRecord r : callRecordMapper.selectList(Wrappers.<CallRecord>lambdaQuery()
                .eq(CallRecord::getMerchantId, merchantId)
                .orderByDesc(CallRecord::getCreateTime)
                .last("limit " + n))) {
            result.add(toRecord("电话联系", r.getTargetType(), r.getTargetId(),
                    r.getOpenid(), r.getCreateTime(), productNames, merchantName));
        }

        result.sort(Comparator.comparing(
                m -> (LocalDateTime) m.get("createTime"),
                Comparator.nullsLast(Comparator.reverseOrder())));
        if (result.size() > n) {
            return result.subList(0, n);
        }
        return result;
    }

    /** 商户产品被浏览 TOP10 */
    public List<Product> merchantHotProduct(Long merchantId) {
        return productMapper.selectList(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchantId)
                .orderByDesc(Product::getViews)
                .last("limit 10"));
    }

    /**
     * 平台趋势：最近 N 天每天的新增浏览/转发/电话/询价/用户/商户
     * days 限制在 7~90，前端只暴露 7 / 30
     */
    public List<Map<String, Object>> trend(int days) {
        int span = Math.max(7, Math.min(days, 90));
        LocalDate start = LocalDate.now().minusDays(span - 1L);
        LocalDateTime from = start.atStartOfDay();

        Map<LocalDate, Long> views = countByDay(viewRecordMapper.selectMaps(new QueryWrapper<ViewRecord>()
                .select("DATE(create_time) AS d", "COUNT(*) AS c")
                .ge("create_time", from)
                .groupBy("DATE(create_time)")));
        Map<LocalDate, Long> forwards = countByDay(forwardRecordMapper.selectMaps(new QueryWrapper<ForwardRecord>()
                .select("DATE(create_time) AS d", "COUNT(*) AS c")
                .ge("create_time", from)
                .groupBy("DATE(create_time)")));
        Map<LocalDate, Long> calls = countByDay(callRecordMapper.selectMaps(new QueryWrapper<CallRecord>()
                .select("DATE(create_time) AS d", "COUNT(*) AS c")
                .ge("create_time", from)
                .groupBy("DATE(create_time)")));
        Map<LocalDate, Long> inquiries = countByDay(inquiryMapper.selectMaps(new QueryWrapper<Inquiry>()
                .select("DATE(create_time) AS d", "COUNT(*) AS c")
                .ge("create_time", from)
                .groupBy("DATE(create_time)")));
        Map<LocalDate, Long> users = countByDay(mpUserMapper.selectMaps(new QueryWrapper<MpUser>()
                .select("DATE(create_time) AS d", "COUNT(*) AS c")
                .ge("create_time", from)
                .groupBy("DATE(create_time)")));
        Map<LocalDate, Long> merchants = countByDay(merchantMapper.selectMaps(new QueryWrapper<Merchant>()
                .select("DATE(create_time) AS d", "COUNT(*) AS c")
                .ge("create_time", from)
                .groupBy("DATE(create_time)")));

        List<Map<String, Object>> list = new ArrayList<>(span);
        for (int i = 0; i < span; i++) {
            LocalDate date = start.plusDays(i);
            Map<String, Object> row = new LinkedHashMap<>(8);
            row.put("date", date.toString());
            row.put("views", views.getOrDefault(date, 0L));
            row.put("forwards", forwards.getOrDefault(date, 0L));
            row.put("calls", calls.getOrDefault(date, 0L));
            row.put("inquiries", inquiries.getOrDefault(date, 0L));
            row.put("newUsers", users.getOrDefault(date, 0L));
            row.put("newMerchants", merchants.getOrDefault(date, 0L));
            list.add(row);
        }
        return list;
    }

    // ---------------- 内部工具 ----------------

    private Map<LocalDate, Long> countByDay(List<Map<String, Object>> rows) {
        Map<LocalDate, Long> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Object dayValue = row.containsKey("d") ? row.get("d") : row.get("D");
            Object countValue = row.containsKey("c") ? row.get("c") : row.get("C");
            LocalDate day = toLocalDate(dayValue);
            if (day != null && countValue instanceof Number number) {
                result.put(day, number.longValue());
            }
        }
        return result;
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate date) {
            return date;
        }
        if (value instanceof java.sql.Date date) {
            return date.toLocalDate();
        }
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.toLocalDate();
        }
        if (value instanceof java.util.Date date) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (value != null) {
            String text = value.toString();
            if (text.length() >= 10) {
                return LocalDate.parse(text.substring(0, 10));
            }
        }
        return null;
    }

    private List<Long> ownProductIds(Long merchantId) {
        List<Product> products = productMapper.selectList(Wrappers.<Product>lambdaQuery()
                .select(Product::getId)
                .eq(Product::getMerchantId, merchantId));
        if (products.isEmpty()) {
            return Collections.singletonList(-1L);
        }
        return products.stream().map(Product::getId).collect(Collectors.toList());
    }

    /** selectMaps 返回的列名大小写因数据库而异，取第一个命中的值 */
    private Object firstValue(Map<String, Object> row, String... keys) {
        for (String key : keys) {
            if (row.containsKey(key) && row.get(key) != null) {
                return row.get(key);
            }
        }
        return null;
    }

    private long countView(Long merchantId, List<Long> productIds) {
        return viewRecordMapper.selectCount(Wrappers.<ViewRecord>lambdaQuery()
                .and(w -> w.eq(ViewRecord::getTargetType, TARGET_MERCHANT)
                        .eq(ViewRecord::getTargetId, merchantId)
                        .or().eq(ViewRecord::getTargetType, TARGET_PRODUCT)
                        .in(ViewRecord::getTargetId, productIds)));
    }

    private long countForward(Long merchantId, List<Long> productIds) {
        return forwardRecordMapper.selectCount(Wrappers.<ForwardRecord>lambdaQuery()
                .and(w -> w.eq(ForwardRecord::getTargetType, TARGET_MERCHANT)
                        .eq(ForwardRecord::getTargetId, merchantId)
                        .or().eq(ForwardRecord::getTargetType, TARGET_PRODUCT)
                        .in(ForwardRecord::getTargetId, productIds)));
    }

    private Map<String, Object> toRecord(String action, Integer targetType, Long targetId, String openid,
                                         LocalDateTime time, Map<Long, String> productNames, String merchantName) {
        Map<String, Object> m = new HashMap<>(8);
        m.put("action", action);
        m.put("targetType", targetType);
        m.put("targetId", targetId);
        m.put("targetName", Integer.valueOf(TARGET_PRODUCT).equals(targetType)
                ? productNames.getOrDefault(targetId, "已删除产品")
                : merchantName);
        m.put("visitor", maskOpenid(openid));
        m.put("createTime", time);
        return m;
    }

    /** 访客标识脱敏：只展示后 6 位 */
    private String maskOpenid(String openid) {
        if (openid == null || openid.isBlank()) {
            return "匿名访客";
        }
        return "访客" + openid.substring(Math.max(0, openid.length() - 6));
    }
}
