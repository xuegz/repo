package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.common.BusinessException;
import com.jiancai.common.CsvUtil;
import com.jiancai.entity.CallRecord;
import com.jiancai.entity.Favorite;
import com.jiancai.entity.ForwardRecord;
import com.jiancai.entity.Inquiry;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.MpUser;
import com.jiancai.entity.Product;
import com.jiancai.entity.ViewRecord;
import com.jiancai.mapper.CallRecordMapper;
import com.jiancai.mapper.FavoriteMapper;
import com.jiancai.mapper.ForwardRecordMapper;
import com.jiancai.mapper.InquiryMapper;
import com.jiancai.mapper.MerchantMapper;
import com.jiancai.mapper.MpUserMapper;
import com.jiancai.mapper.ProductMapper;
import com.jiancai.mapper.ViewRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 平台端小程序用户：详情、备注、导出
 */
@Service
public class MpUserAdminService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] EXPORT_HEADERS = {
            "ID", "昵称", "OpenID", "备注", "首次登录", "最近登录", "浏览数", "收藏数", "询价数"
    };

    @Autowired
    private MpUserMapper mpUserMapper;

    @Autowired
    private ViewRecordMapper viewRecordMapper;

    @Autowired
    private ForwardRecordMapper forwardRecordMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private CallRecordMapper callRecordMapper;

    @Autowired
    private InquiryMapper inquiryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    public Map<String, Object> detail(String openid) {
        MpUser user = findByOpenid(openid);

        List<ViewRecord> views = viewRecordMapper.selectList(Wrappers.<ViewRecord>lambdaQuery()
                .eq(ViewRecord::getOpenid, openid)
                .orderByDesc(ViewRecord::getCreateTime)
                .last("limit 20"));
        List<Favorite> favorites = favoriteMapper.selectList(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getOpenid, openid)
                .orderByDesc(Favorite::getCreateTime)
                .last("limit 20"));
        List<CallRecord> calls = callRecordMapper.selectList(Wrappers.<CallRecord>lambdaQuery()
                .eq(CallRecord::getOpenid, openid)
                .orderByDesc(CallRecord::getCreateTime)
                .last("limit 20"));
        List<Inquiry> inquiries = inquiryMapper.selectList(Wrappers.<Inquiry>lambdaQuery()
                .eq(Inquiry::getOpenid, openid)
                .orderByDesc(Inquiry::getCreateTime)
                .last("limit 20"));

        Map<Long, String> productNames = productNames(views, favorites, calls, inquiries);
        Map<Long, String> merchantNames = merchantNames(views, favorites, calls, inquiries);

        Map<String, Object> counts = new HashMap<>(8);
        counts.put("views", viewRecordMapper.selectCount(Wrappers.<ViewRecord>lambdaQuery()
                .eq(ViewRecord::getOpenid, openid)));
        counts.put("forwards", forwardRecordMapper.selectCount(Wrappers.<ForwardRecord>lambdaQuery()
                .eq(ForwardRecord::getOpenid, openid)));
        counts.put("calls", callRecordMapper.selectCount(Wrappers.<CallRecord>lambdaQuery()
                .eq(CallRecord::getOpenid, openid)));
        counts.put("favorites", favoriteMapper.selectCount(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getOpenid, openid)));
        counts.put("inquiries", inquiryMapper.selectCount(Wrappers.<Inquiry>lambdaQuery()
                .eq(Inquiry::getOpenid, openid)));

        List<Map<String, Object>> recentViews = new ArrayList<>();
        for (ViewRecord record : views) {
            recentViews.add(activity("浏览", record.getTargetType(), record.getTargetId(),
                    record.getCreateTime(), productNames, merchantNames));
        }
        List<Map<String, Object>> recentFavorites = new ArrayList<>();
        for (Favorite favorite : favorites) {
            recentFavorites.add(activity("收藏", favorite.getTargetType(), favorite.getTargetId(),
                    favorite.getCreateTime(), productNames, merchantNames));
        }
        List<Map<String, Object>> recentCalls = new ArrayList<>();
        for (CallRecord record : calls) {
            recentCalls.add(activity("电话", record.getTargetType(), record.getTargetId(),
                    record.getCreateTime(), productNames, merchantNames));
        }
        List<Map<String, Object>> recentInquiries = new ArrayList<>();
        for (Inquiry inquiry : inquiries) {
            Map<String, Object> row = new HashMap<>(8);
            row.put("id", inquiry.getId());
            row.put("productId", inquiry.getProductId());
            row.put("productName", productNames.getOrDefault(inquiry.getProductId(), "已删除产品"));
            row.put("merchantName", merchantNames.getOrDefault(inquiry.getMerchantId(), "未知商户"));
            row.put("status", AdminInquiryService.statusText(inquiry.getStatus()));
            row.put("quoteTotal", inquiry.getQuoteTotal());
            row.put("contact", inquiry.getContact());
            row.put("createTime", inquiry.getCreateTime());
            recentInquiries.add(row);
        }

        Map<String, Object> map = new HashMap<>(8);
        map.put("user", user);
        map.put("counts", counts);
        map.put("recentViews", recentViews);
        map.put("recentFavorites", recentFavorites);
        map.put("recentCalls", recentCalls);
        map.put("recentInquiries", recentInquiries);
        return map;
    }

    public void saveRemark(String openid, String remark) {
        MpUser user = findByOpenid(openid);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        String value = remark == null ? "" : remark.trim();
        if (value.length() > 200) {
            throw new BusinessException("备注不能超过 200 字");
        }
        user.setRemark(value);
        user.setUpdateTime(LocalDateTime.now());
        mpUserMapper.updateById(user);
    }

    public byte[] exportCsv(String keyword) {
        var query = Wrappers.<MpUser>lambdaQuery();
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            query.and(w -> w.like(MpUser::getNickname, kw).or().like(MpUser::getOpenid, kw));
        }
        query.orderByDesc(MpUser::getLastLoginTime).last("limit 5000");
        List<MpUser> users = mpUserMapper.selectList(query);
        List<String> openids = users.stream().map(MpUser::getOpenid).filter(Objects::nonNull).toList();
        Map<String, Long> viewCounts = openids.isEmpty() ? Collections.emptyMap()
                : countByOpenid(viewRecordMapper.selectMaps(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ViewRecord>()
                        .select("openid", "COUNT(*) AS c").in("openid", openids).groupBy("openid")));
        Map<String, Long> favoriteCounts = openids.isEmpty() ? Collections.emptyMap()
                : countByOpenid(favoriteMapper.selectMaps(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Favorite>()
                        .select("openid", "COUNT(*) AS c").in("openid", openids).groupBy("openid")));
        Map<String, Long> inquiryCounts = openids.isEmpty() ? Collections.emptyMap()
                : countByOpenid(inquiryMapper.selectMaps(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Inquiry>()
                        .select("openid", "COUNT(*) AS c").in("openid", openids).groupBy("openid")));

        List<String[]> rows = new ArrayList<>(users.size());
        for (MpUser user : users) {
            rows.add(new String[]{
                    String.valueOf(user.getId()),
                    user.getNickname(),
                    user.getOpenid(),
                    user.getRemark(),
                    format(user.getCreateTime()),
                    format(user.getLastLoginTime()),
                    String.valueOf(viewCounts.getOrDefault(user.getOpenid(), 0L)),
                    String.valueOf(favoriteCounts.getOrDefault(user.getOpenid(), 0L)),
                    String.valueOf(inquiryCounts.getOrDefault(user.getOpenid(), 0L))
            });
        }
        return CsvUtil.build(EXPORT_HEADERS, rows);
    }

    private Map<String, Long> countByOpenid(List<Map<String, Object>> rows) {
        Map<String, Long> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Object openid = row.containsKey("openid") ? row.get("openid") : row.get("OPENID");
            Object count = row.containsKey("c") ? row.get("c") : row.get("C");
            if (openid != null && count instanceof Number number) {
                result.put(openid.toString(), number.longValue());
            }
        }
        return result;
    }

    private MpUser findByOpenid(String openid) {
        if (openid == null || openid.isBlank()) {
            throw new BusinessException("请指定用户 openid");
        }
        MpUser user = mpUserMapper.selectOne(Wrappers.<MpUser>lambdaQuery()
                .eq(MpUser::getOpenid, openid.trim()));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private Map<Long, String> productNames(List<ViewRecord> views, List<Favorite> favorites,
                                           List<CallRecord> calls, List<Inquiry> inquiries) {
        Set<Long> ids = new java.util.HashSet<>();
        views.stream().filter(v -> Objects.equals(v.getTargetType(), 1))
                .forEach(v -> ids.add(v.getTargetId()));
        favorites.stream().filter(f -> Objects.equals(f.getTargetType(), 1))
                .forEach(f -> ids.add(f.getTargetId()));
        calls.stream().filter(c -> Objects.equals(c.getTargetType(), 1))
                .forEach(c -> ids.add(c.getTargetId()));
        inquiries.stream().map(Inquiry::getProductId).filter(Objects::nonNull).forEach(ids::add);
        ids.remove(null);
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return productMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Product::getId,
                        p -> p.getName() == null ? "未命名产品" : p.getName(), (a, b) -> a));
    }

    private Map<Long, String> merchantNames(List<ViewRecord> views, List<Favorite> favorites,
                                            List<CallRecord> calls, List<Inquiry> inquiries) {
        Set<Long> ids = new java.util.HashSet<>();
        views.stream().filter(v -> Objects.equals(v.getTargetType(), 2))
                .forEach(v -> ids.add(v.getTargetId()));
        favorites.stream().filter(f -> Objects.equals(f.getTargetType(), 2))
                .forEach(f -> ids.add(f.getTargetId()));
        calls.stream().filter(c -> Objects.equals(c.getTargetType(), 2))
                .forEach(c -> ids.add(c.getTargetId()));
        inquiries.stream().map(Inquiry::getMerchantId).filter(Objects::nonNull).forEach(ids::add);
        ids.remove(null);
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return merchantMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Merchant::getId,
                        m -> m.getName() == null ? "未命名商户" : m.getName(), (a, b) -> a));
    }

    private Map<String, Object> activity(String action, Integer targetType, Long targetId,
                                         LocalDateTime time, Map<Long, String> productNames,
                                         Map<Long, String> merchantNames) {
        Map<String, Object> row = new HashMap<>(6);
        row.put("action", action);
        row.put("targetType", targetType);
        row.put("targetId", targetId);
        row.put("targetName", Objects.equals(targetType, 1)
                ? productNames.getOrDefault(targetId, "已删除产品")
                : merchantNames.getOrDefault(targetId, "已删除商户"));
        row.put("createTime", time);
        return row;
    }

    private static String format(LocalDateTime time) {
        return time == null ? "" : time.format(TIME_FORMAT);
    }
}
