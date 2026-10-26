package com.jiancai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Category;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.mapper.CategoryMapper;
import com.jiancai.mapper.MerchantMapper;
import com.jiancai.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 产品业务
 */
@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    /** 管理端分页（含展示信息） */
    public IPage<Product> pageWithInfo(IPage<Product> page, String name, Long categoryId,
                                       Integer status, Long merchantId) {
        IPage<Product> result = page(page, Wrappers.<Product>lambdaQuery()
                .like(name != null && !name.isBlank(), Product::getName, name)
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .eq(status != null, Product::getStatus, status)
                .eq(merchantId != null, Product::getMerchantId, merchantId)
                .orderByDesc(Product::getCreateTime));
        fillInfo(result.getRecords());
        return result;
    }

    /** 填充品类名称、商户名称、图片数组 */
    public void fillInfo(List<Product> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<Long, String> catMap = categoryMapper.selectList(null).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
        Map<Long, Merchant> merchantMap = merchantMapper.selectList(null).stream()
                .collect(Collectors.toMap(Merchant::getId, m -> m, (a, b) -> a));

        for (Product p : list) {
            if (p.getCategoryId() != null) {
                p.setCategoryName(catMap.get(p.getCategoryId()));
            }
            Merchant m = merchantMap.get(p.getMerchantId());
            if (m != null) {
                p.setMerchantName(m.getName());
                p.setMerchantPhone(m.getPhone());
            }
            p.setImageList(splitImages(p.getImages()));
        }
    }

    public static List<String> splitImages(String images) {
        if (images == null || images.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(images.split(","))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    /** 详情（含展示信息） */
    public Product getDetail(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        fillInfo(Collections.singletonList(product));
        return product;
    }

    /** 保存前把图片数组合并为逗号分隔字符串 */
    public void prepareSave(Product product) {
        if (product.getImageList() != null && !product.getImageList().isEmpty()) {
            product.setImages(String.join(",", product.getImageList()));
        }
        if (product.getStatus() == null) {
            product.setStatus(0);
        }
    }

    /** 审核产品 */
    public void audit(Long id, Integer status, String rejectReason) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        product.setStatus(status);
        if (Integer.valueOf(2).equals(status)) {
            product.setRejectReason(rejectReason);
        } else {
            product.setRejectReason(null);
        }
        updateById(product);
    }

    /** 上下架（商户端操作，仅对已通过的产品生效） */
    public void changeStatus(Long id, Integer status) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        product.setStatus(status);
        updateById(product);
    }
}
