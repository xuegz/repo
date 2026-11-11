package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Inquiry;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.mapper.InquiryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 小程序询价线索：提交（会员专享）与「我的询价」查询
 */
@Service
@RequiredArgsConstructor
public class MpInquiryService {

    private final InquiryMapper inquiryMapper;
    private final ProductService productService;
    private final MerchantService merchantService;
    private final MemberAccessService memberAccessService;

    /** 提交询价线索：客户留资，商户端跟进（会员专享） */
    public void submit(String openid, Long productId, Long merchantId,
                       String quantity, String contact, String remark, String expectDate) {
        memberAccessService.requireMember(openid, "在线询价");
        if (contact == null || contact.isBlank()) {
            throw new BusinessException("请填写联系方式");
        }
        Long targetMerchantId = merchantId;
        if (targetMerchantId == null && productId != null) {
            Product product = productService.getById(productId);
            targetMerchantId = product == null ? null : product.getMerchantId();
        }
        if (targetMerchantId == null) {
            throw new BusinessException("未找到对应商户");
        }

        Inquiry inquiry = new Inquiry();
        inquiry.setProductId(productId);
        inquiry.setMerchantId(targetMerchantId);
        inquiry.setQuantity(quantity);
        inquiry.setContact(contact.trim());
        inquiry.setRemark(remark);
        inquiry.setExpectDate(expectDate);
        inquiry.setOpenid(openid);
        inquiry.setStatus(0);
        inquiryMapper.insert(inquiry);
    }

    /** 我的询价（含商户报价；非会员隐藏商户电话） */
    public List<Inquiry> myInquiries(String openid, Integer status) {
        List<Inquiry> list = inquiryMapper.selectList(Wrappers.<Inquiry>lambdaQuery()
                .eq(Inquiry::getOpenid, openid)
                .eq(status != null, Inquiry::getStatus, status)
                .orderByDesc(Inquiry::getCreateTime)
                .last("limit 50"));
        fillInquiryInfo(list);
        if (!memberAccessService.isMember(openid)) {
            list.forEach(i -> i.setMerchantPhone(null));
        }
        return list;
    }

    private void fillInquiryInfo(List<Inquiry> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<Long> productIds = list.stream().map(Inquiry::getProductId)
                .filter(Objects::nonNull).distinct().toList();
        Map<Long, Product> productMap = productIds.isEmpty() ? Map.of()
                : productService.listByIds(productIds).stream()
                        .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));
        List<Long> merchantIds = list.stream().map(Inquiry::getMerchantId)
                .filter(Objects::nonNull).distinct().toList();
        Map<Long, Merchant> merchantMap = merchantIds.isEmpty() ? Map.of()
                : merchantService.listByIds(merchantIds).stream()
                        .collect(Collectors.toMap(Merchant::getId, m -> m, (a, b) -> a));
        for (Inquiry i : list) {
            Product p = productMap.get(i.getProductId());
            if (p != null) {
                i.setProductName(p.getName());
                List<String> images = ProductService.splitImages(p.getImages());
                i.setProductImage(images.isEmpty() ? null : images.get(0));
            }
            Merchant m = merchantMap.get(i.getMerchantId());
            if (m != null) {
                i.setMerchantName(m.getName());
                i.setMerchantPhone(m.getPhone());
            }
        }
    }
}
