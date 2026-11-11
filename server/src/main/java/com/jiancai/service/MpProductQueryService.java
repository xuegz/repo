package com.jiancai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Market;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 小程序产品查询：列表 / 详情 / 比价搜索 / 同款比价
 */
@Service
@RequiredArgsConstructor
public class MpProductQueryService {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final MarketService marketService;
    private final MerchantService merchantService;
    private final MemberAccessService memberAccessService;

    /** 产品列表（分页 / 关键词 匹配名称+品牌+产地 / 品类 / 市场 / 价格排序） */
    public IPage<Product> pageProducts(long pageNum, long pageSize, String keyword,
                                       Long categoryId, String market, String sort, String openid) {
        var wrapper = Wrappers.<Product>lambdaQuery()
                .eq(Product::getStatus, 1);
        // 选中一级品类时，其下所有子类的产品一并展示
        if (categoryId != null) {
            wrapper.in(Product::getCategoryId, categoryService.subtreeIds(categoryId));
        }

        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or().like(Product::getBrand, keyword)
                    .or().like(Product::getOrigin, keyword));
        }

        if (market != null && !market.isBlank()) {
            Market target = marketService.getOne(Wrappers.<Market>lambdaQuery()
                    .eq(Market::getName, market), false);
            var merchantQuery = Wrappers.<Merchant>lambdaQuery();
            if (target != null) {
                merchantQuery.eq(Merchant::getMarketId, target.getId());
            } else {
                merchantQuery.like(Merchant::getMarket, market);
            }
            List<Long> merchantIds = merchantService.list(merchantQuery)
                    .stream().map(Merchant::getId).toList();
            if (merchantIds.isEmpty()) {
                return new Page<>(pageNum, pageSize);
            }
            wrapper.in(Product::getMerchantId, merchantIds);
        }
        if ("price_asc".equals(sort)) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc(Product::getPrice);
        } else {
            wrapper.orderByDesc(Product::getCreateTime);
        }

        IPage<Product> page = productService.page(new Page<>(pageNum, pageSize), wrapper);
        productService.fillInfo(page.getRecords());
        memberAccessService.maskProducts(page.getRecords(), memberAccessService.isMember(openid));
        return page;
    }

    /** 产品详情（非会员隐藏商户联系方式） */
    public Product detail(Long id, String openid) {
        Product product = productService.getById(id);
        if (product == null || !Integer.valueOf(1).equals(product.getStatus())) {
            throw new BusinessException("产品不存在或已下架");
        }
        productService.fillInfo(List.of(product));
        memberAccessService.maskProduct(product, memberAccessService.isMember(openid));
        return product;
    }

    /** 比价搜索：输入产品名或品牌，返回各家报价（按价格从低到高，会员专享） */
    public List<Product> priceCompare(String keyword, String openid) {
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException("请输入要比价的产品名称");
        }
        memberAccessService.requireMember(openid, "同款比价");
        List<Product> list = productService.list(Wrappers.<Product>lambdaQuery()
                .eq(Product::getStatus, 1)
                .and(w -> w.like(Product::getName, keyword)
                        .or().like(Product::getBrand, keyword))
                .orderByAsc(Product::getPrice)
                .last("limit 50"));
        productService.fillInfo(list);
        return list;
    }

    /**
     * 同款比价：与指定产品同名，或同品类+品牌+类型的其他商户报价（会员专享）
     * 匹配优先级：同名 > 同品类+品牌+类型 > 同品类
     */
    public List<Product> compare(Long id, String openid) {
        memberAccessService.requireMember(openid, "同款比价");
        Product base = productService.getById(id);
        if (base == null || !Integer.valueOf(1).equals(base.getStatus())) {
            throw new BusinessException("产品不存在或已下架");
        }
        List<Product> list = new ArrayList<>();
        if (base.getName() != null && !base.getName().isBlank()) {
            list = productService.list(Wrappers.<Product>lambdaQuery()
                    .eq(Product::getStatus, 1)
                    .ne(Product::getId, id)
                    .like(Product::getName, base.getName())
                    .orderByAsc(Product::getPrice)
                    .last("limit 20"));
        }
        if (list.isEmpty() && base.getCategoryId() != null) {
            list = productService.list(Wrappers.<Product>lambdaQuery()
                    .eq(Product::getStatus, 1)
                    .ne(Product::getId, id)
                    .eq(Product::getCategoryId, base.getCategoryId())
                    .eq(base.getBrand() != null && !base.getBrand().isBlank(), Product::getBrand, base.getBrand())
                    .eq(base.getTypeName() != null && !base.getTypeName().isBlank(), Product::getTypeName, base.getTypeName())
                    .orderByAsc(Product::getPrice)
                    .last("limit 20"));
        }
        if (list.isEmpty() && base.getCategoryId() != null) {
            list = productService.list(Wrappers.<Product>lambdaQuery()
                    .eq(Product::getStatus, 1)
                    .ne(Product::getId, id)
                    .eq(Product::getCategoryId, base.getCategoryId())
                    .orderByAsc(Product::getPrice)
                    .last("limit 20"));
        }
        productService.fillInfo(list);
        return list;
    }
}
