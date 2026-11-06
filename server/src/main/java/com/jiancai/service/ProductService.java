package com.jiancai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.mapper.MerchantMapper;
import com.jiancai.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private MerchantMapper merchantMapper;

    @Autowired
    private CategoryService categoryService;

    /** 管理端分页（含展示信息） */
    public IPage<Product> pageWithInfo(IPage<Product> page, String name, Long categoryId,
                                       Integer status, Long merchantId) {
        return pageWithInfo(page, name, categoryId, status, merchantId, null);
    }

    /** 商户端分页（含展示信息，支持按浏览/转发/电话/价格排序） */
    public IPage<Product> pageWithInfo(IPage<Product> page, String name, Long categoryId,
                                       Integer status, Long merchantId, String sort) {
        var wrapper = Wrappers.<Product>lambdaQuery()
                .like(name != null && !name.isBlank(), Product::getName, name)
                .eq(status != null, Product::getStatus, status)
                .eq(merchantId != null, Product::getMerchantId, merchantId);
        // 选中一级/二级品类时，其下所有子类的产品一并展示
        if (categoryId != null) {
            wrapper.in(Product::getCategoryId, categoryService.subtreeIds(categoryId));
        }
        applySort(wrapper, sort);
        IPage<Product> result = page(page, wrapper);
        fillInfo(result.getRecords());
        return result;
    }

    private void applySort(LambdaQueryWrapper<Product> wrapper, String sort) {
        if (sort == null || sort.isBlank()) {
            wrapper.orderByDesc(Product::getCreateTime);
            return;
        }
        switch (sort) {
            case "views" -> wrapper.orderByDesc(Product::getViews);
            case "forwards" -> wrapper.orderByDesc(Product::getForwards);
            case "calls" -> wrapper.orderByDesc(Product::getCalls);
            case "price_asc" -> wrapper.orderByAsc(Product::getPrice);
            case "price_desc" -> wrapper.orderByDesc(Product::getPrice);
            default -> wrapper.orderByDesc(Product::getCreateTime);
        }
    }

    /** 填充品类名称、商户名称、图片数组 */
    public void fillInfo(List<Product> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<Long, String> catMap = categoryService.nameMap();
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
                String addr = ((m.getProvince() == null ? "" : m.getProvince())
                        + (m.getCity() == null ? "" : m.getCity())
                        + (m.getAddress() == null ? "" : m.getAddress()));
                p.setMerchantAddress(addr);
                p.setMerchantLatitude(m.getLatitude());
                p.setMerchantLongitude(m.getLongitude());
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
        if (product.getImages() == null && product.getImageList() != null && !product.getImageList().isEmpty()) {
            product.setImages(String.join(",", product.getImageList()));
        }
        if (product.getStatus() == null) {
            product.setStatus(0);
        }
        product.setUpdateTime(LocalDateTime.now());
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
        product.setUpdateTime(LocalDateTime.now());
        updateById(product);
    }

    /** 上下架（商户端操作，仅对已通过的产品生效） */
    public void changeStatus(Long id, Integer status) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        updateById(product);
    }
}
