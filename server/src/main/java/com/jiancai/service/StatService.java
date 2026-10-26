package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.entity.ForwardRecord;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.entity.ViewRecord;
import com.jiancai.mapper.ForwardRecordMapper;
import com.jiancai.mapper.MerchantMapper;
import com.jiancai.mapper.ProductMapper;
import com.jiancai.mapper.ViewRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 浏览 / 转发统计业务
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
    private ProductMapper productMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    /** 记录浏览 */
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
        map.put("productTotal", productMapper.selectCount(null));
        map.put("productPending", productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                .eq(Product::getStatus, 0)));
        map.put("viewTotal", viewRecordMapper.selectCount(null));
        map.put("forwardTotal", forwardRecordMapper.selectCount(null));
        return map;
    }

    /** 商户端数据概览 */
    public Map<String, Object> merchantOverview(Long merchantId) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("productTotal", productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchantId)));
        map.put("productOnSale", productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchantId).eq(Product::getStatus, 1)));
        map.put("productPending", productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchantId).eq(Product::getStatus, 0)));
        map.put("viewTotal", viewRecordMapper.selectCount(Wrappers.<ViewRecord>lambdaQuery()
                .eq(ViewRecord::getTargetType, TARGET_MERCHANT)
                .eq(ViewRecord::getTargetId, merchantId)));
        map.put("forwardTotal", forwardRecordMapper.selectCount(Wrappers.<ForwardRecord>lambdaQuery()
                .eq(ForwardRecord::getTargetType, TARGET_MERCHANT)
                .eq(ForwardRecord::getTargetId, merchantId)));

        Merchant merchant = merchantMapper.selectById(merchantId);
        map.put("merchantViews", merchant == null ? 0 : merchant.getViews());
        map.put("expireTime", merchant == null ? null : merchant.getExpireTime());
        return map;
    }

    /** 商户产品被浏览 TOP10 */
    public List<Product> merchantHotProduct(Long merchantId) {
        return productMapper.selectList(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, merchantId)
                .orderByDesc(Product::getViews)
                .last("limit 10"));
    }
}
