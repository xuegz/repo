package com.jiancai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.CsvUtil;
import com.jiancai.entity.Inquiry;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.MpUser;
import com.jiancai.entity.Product;
import com.jiancai.mapper.InquiryMapper;
import com.jiancai.mapper.MerchantMapper;
import com.jiancai.mapper.MpUserMapper;
import com.jiancai.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 平台端询价线索：跨商户查询、概览与导出
 */
@Service
public class AdminInquiryService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] EXPORT_HEADERS = {
            "ID", "提交时间", "商品", "商户", "商户电话", "用户昵称", "OpenID", "联系方式",
            "数量", "期望到货", "需求说明", "状态", "报价单价", "报价总价", "报价说明", "报价有效期", "报价时间"
    };

    @Autowired
    private InquiryMapper inquiryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MpUserMapper mpUserMapper;

    public IPage<Inquiry> page(long pageNum, long pageSize, String keyword, Integer status,
                               Long merchantId, String merchantName,
                               LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Inquiry> query = filter(keyword, status, merchantId, merchantName, startDate, endDate);
        query.orderByDesc(Inquiry::getCreateTime);
        IPage<Inquiry> page = inquiryMapper.selectPage(new Page<>(pageNum, pageSize), query);
        fillInfo(page.getRecords());
        return page;
    }

    public Map<String, Object> overview() {
        Map<String, Object> map = new HashMap<>(12);
        long total = inquiryMapper.selectCount(null);
        long pending = inquiryMapper.selectCount(Wrappers.<Inquiry>lambdaQuery().eq(Inquiry::getStatus, 0));
        long quoted = inquiryMapper.selectCount(Wrappers.<Inquiry>lambdaQuery().eq(Inquiry::getStatus, 1));
        long follow = inquiryMapper.selectCount(Wrappers.<Inquiry>lambdaQuery().eq(Inquiry::getStatus, 2));
        map.put("total", total);
        map.put("pending", pending);
        map.put("quoted", quoted);
        map.put("follow", follow);
        map.put("today", inquiryMapper.selectCount(Wrappers.<Inquiry>lambdaQuery()
                .ge(Inquiry::getCreateTime, LocalDate.now().atStartOfDay())));
        map.put("quoteRate", total == 0 ? 0 : Math.round((quoted + follow) * 1000.0 / total) / 10.0);
        return map;
    }

    public byte[] exportCsv(String keyword, Integer status, Long merchantId, String merchantName,
                            LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Inquiry> query = filter(keyword, status, merchantId, merchantName, startDate, endDate);
        query.orderByDesc(Inquiry::getCreateTime).last("limit 5000");
        List<Inquiry> list = inquiryMapper.selectList(query);
        fillInfo(list);
        List<String[]> rows = new ArrayList<>(list.size());
        for (Inquiry inquiry : list) {
            rows.add(new String[]{
                    String.valueOf(inquiry.getId()),
                    format(inquiry.getCreateTime()),
                    inquiry.getProductName(),
                    inquiry.getMerchantName(),
                    inquiry.getMerchantPhone(),
                    inquiry.getUserNickname(),
                    inquiry.getOpenid(),
                    inquiry.getContact(),
                    inquiry.getQuantity(),
                    inquiry.getExpectDate(),
                    inquiry.getRemark(),
                    statusText(inquiry.getStatus()),
                    plain(inquiry.getQuotePrice()),
                    plain(inquiry.getQuoteTotal()),
                    inquiry.getQuoteRemark(),
                    inquiry.getQuoteValidUntil() == null ? "" : inquiry.getQuoteValidUntil().toString(),
                    format(inquiry.getQuoteTime())
            });
        }
        return CsvUtil.build(EXPORT_HEADERS, rows);
    }

    private LambdaQueryWrapper<Inquiry> filter(String keyword, Integer status, Long merchantId,
                                               String merchantName, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Inquiry> query = Wrappers.lambdaQuery();
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            query.and(w -> w.like(Inquiry::getContact, kw)
                    .or().like(Inquiry::getRemark, kw)
                    .or().like(Inquiry::getOpenid, kw));
        }
        query.eq(status != null, Inquiry::getStatus, status);
        query.eq(merchantId != null, Inquiry::getMerchantId, merchantId);
        if (merchantName != null && !merchantName.isBlank()) {
            List<Long> ids = merchantMapper.selectList(Wrappers.<Merchant>lambdaQuery()
                            .like(Merchant::getName, merchantName.trim()))
                    .stream().map(Merchant::getId).toList();
            if (ids.isEmpty()) {
                query.eq(Inquiry::getId, -1L);
            } else {
                query.in(Inquiry::getMerchantId, ids);
            }
        }
        query.ge(startDate != null, Inquiry::getCreateTime,
                startDate == null ? null : startDate.atStartOfDay());
        query.lt(endDate != null, Inquiry::getCreateTime,
                endDate == null ? null : endDate.plusDays(1).atStartOfDay());
        return query;
    }

    private void fillInfo(List<Inquiry> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<Long> productIds = list.stream().map(Inquiry::getProductId)
                .filter(Objects::nonNull).distinct().toList();
        Map<Long, Product> productMap = productIds.isEmpty() ? Collections.emptyMap()
                : productMapper.selectBatchIds(productIds).stream()
                        .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));
        List<Long> merchantIds = list.stream().map(Inquiry::getMerchantId)
                .filter(Objects::nonNull).distinct().toList();
        Map<Long, Merchant> merchantMap = merchantIds.isEmpty() ? Collections.emptyMap()
                : merchantMapper.selectBatchIds(merchantIds).stream()
                        .collect(Collectors.toMap(Merchant::getId, m -> m, (a, b) -> a));
        List<String> openids = list.stream().map(Inquiry::getOpenid)
                .filter(Objects::nonNull).filter(s -> !s.isBlank()).distinct().toList();
        Map<String, String> nicknameMap = openids.isEmpty() ? Collections.emptyMap()
                : mpUserMapper.selectList(Wrappers.<MpUser>lambdaQuery()
                                .in(MpUser::getOpenid, openids))
                        .stream()
                        .filter(u -> u.getNickname() != null && !u.getNickname().isBlank())
                        .collect(Collectors.toMap(MpUser::getOpenid, MpUser::getNickname, (a, b) -> a));

        for (Inquiry inquiry : list) {
            Product product = productMap.get(inquiry.getProductId());
            if (product != null) {
                inquiry.setProductName(product.getName());
                List<String> images = ProductService.splitImages(product.getImages());
                inquiry.setProductImage(images.isEmpty() ? null : images.get(0));
            }
            Merchant merchant = merchantMap.get(inquiry.getMerchantId());
            if (merchant != null) {
                inquiry.setMerchantName(merchant.getName());
                inquiry.setMerchantPhone(merchant.getPhone());
            }
            if (inquiry.getOpenid() != null) {
                inquiry.setUserNickname(nicknameMap.get(inquiry.getOpenid()));
            }
        }
    }

    public static String statusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "待报价";
            case 1 -> "已报价";
            case 2 -> "跟进中";
            default -> "未知";
        };
    }

    private static String plain(java.math.BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }

    private static String format(LocalDateTime time) {
        return time == null ? "" : time.format(TIME_FORMAT);
    }
}
