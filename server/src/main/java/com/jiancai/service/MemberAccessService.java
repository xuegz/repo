package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.MpMerchantBind;
import com.jiancai.entity.Product;
import com.jiancai.mapper.MpMerchantBindMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家会员权益：绑定商户账号且会员未过期的用户，才可查看联系方式、位置、比价与在线咨询
 */
@Service
@RequiredArgsConstructor
public class MemberAccessService {

    private final MpMerchantBindMapper bindMapper;
    private final MerchantService merchantService;

    /** 当前微信绑定且在线的商户；未绑定或商户未上线返回 null */
    public Merchant boundMerchant(String openid) {
        String value = normalize(openid);
        if (value == null) {
            return null;
        }
        MpMerchantBind bind = bindMapper.selectOne(Wrappers.<MpMerchantBind>lambdaQuery()
                .eq(MpMerchantBind::getOpenid, value));
        if (bind == null || bind.getMerchantId() == null) {
            return null;
        }
        Merchant merchant = merchantService.getById(bind.getMerchantId());
        if (merchant == null || !Integer.valueOf(1).equals(merchant.getStatus())) {
            return null;
        }
        return merchant;
    }

    public boolean isMember(String openid) {
        Merchant merchant = boundMerchant(openid);
        return merchant != null && merchant.getExpireTime() != null
                && merchant.getExpireTime().isAfter(LocalDateTime.now());
    }

    /** 会员状态：供小程序判断展示锁定态与绑定/续费引导 */
    public Map<String, Object> status(String openid) {
        Map<String, Object> data = new HashMap<>(6);
        Merchant merchant = boundMerchant(openid);
        boolean bound = merchant != null;
        data.put("bound", bound);
        data.put("memberActive", bound && merchant.getExpireTime() != null
                && merchant.getExpireTime().isAfter(LocalDateTime.now()));
        if (bound) {
            data.put("merchantId", merchant.getId());
            data.put("shopName", merchant.getName());
            data.put("expireTime", merchant.getExpireTime());
        }
        return data;
    }

    public void requireMember(String openid, String feature) {
        if (!isMember(openid)) {
            throw new BusinessException(403, feature + "为会员专享，请先绑定商户账号并开通会员");
        }
    }

    /** 非会员隐藏商户电话、详细地址与坐标，只保留可浏览的公开信息 */
    public void maskProduct(Product product, boolean member) {
        if (product == null) {
            return;
        }
        product.setMemberActive(member);
        if (member) {
            return;
        }
        product.setMerchantPhone(null);
        product.setMerchantAddress(null);
        product.setMerchantLatitude(null);
        product.setMerchantLongitude(null);
    }

    public void maskProducts(List<Product> products, boolean member) {
        if (products == null) {
            return;
        }
        for (Product product : products) {
            maskProduct(product, member);
        }
    }

    public void maskMerchant(Merchant merchant, boolean member) {
        if (merchant == null) {
            return;
        }
        merchant.setMemberActive(member);
        if (member) {
            return;
        }
        merchant.setContactName(null);
        merchant.setPhone(null);
        merchant.setAddress(null);
        merchant.setLatitude(null);
        merchant.setLongitude(null);
        merchant.setDistance(null);
    }

    public void maskMerchants(List<Merchant> merchants, boolean member) {
        if (merchants == null) {
            return;
        }
        for (Merchant merchant : merchants) {
            maskMerchant(merchant, member);
        }
    }

    private static String normalize(String openid) {
        if (openid == null || openid.isBlank()) {
            return null;
        }
        String value = openid.trim();
        if (value.length() > 64) {
            return null;
        }
        return value;
    }
}
