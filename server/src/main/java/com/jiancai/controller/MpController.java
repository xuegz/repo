package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.Category;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.entity.ViewRecord;
import com.jiancai.mapper.ViewRecordMapper;
import com.jiancai.service.CategoryService;
import com.jiancai.service.MerchantService;
import com.jiancai.service.ProductService;
import com.jiancai.service.SiteService;
import com.jiancai.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 小程序端接口（无需登录）
 */
@RestController
@RequestMapping("/api/mp")
public class MpController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private StatService statService;

    @Autowired
    private ViewRecordMapper viewRecordMapper;

    /** 首页聚合：轮播 + 广告位 + 品类 + 推荐产品 */
    @GetMapping("/home")
    public R<Map<String, Object>> home() {
        Map<String, Object> data = new HashMap<>(8);
        data.put("banners", siteService.listBanner());
        data.put("ads", siteService.listAdByPosition("home"));
        data.put("categories", categoryService.listEnabled());

        List<Product> recommends = productService.list(Wrappers.<Product>lambdaQuery()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreateTime)
                .last("limit 10"));
        productService.fillInfo(recommends);
        data.put("recommends", recommends);
        return R.ok(data);
    }

    @GetMapping("/categories")
    public R<List<Category>> categories() {
        return R.ok(categoryService.listEnabled());
    }

    /** 产品列表（分页 / 关键词 / 品类 / 市场筛选） */
    @GetMapping("/products")
    public R<IPage<Product>> products(@RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) String market) {
        var wrapper = Wrappers.<Product>lambdaQuery()
                .eq(Product::getStatus, 1)
                .like(keyword != null && !keyword.isBlank(), Product::getName, keyword)
                .eq(categoryId != null, Product::getCategoryId, categoryId);

        if (market != null && !market.isBlank()) {
            List<Long> merchantIds = merchantService.list(Wrappers.<Merchant>lambdaQuery()
                            .like(Merchant::getMarket, market))
                    .stream().map(Merchant::getId).toList();
            if (merchantIds.isEmpty()) {
                return R.ok(new Page<>(pageNum, pageSize));
            }
            wrapper.in(Product::getMerchantId, merchantIds);
        }
        wrapper.orderByDesc(Product::getCreateTime);

        IPage<Product> page = productService.page(new Page<>(pageNum, pageSize), wrapper);
        productService.fillInfo(page.getRecords());
        return R.ok(page);
    }

    /** 产品详情 */
    @GetMapping("/product")
    public R<Product> product(@RequestParam Long id) {
        return R.ok(productService.getDetail(id));
    }

    /** 商户详情（含在售产品） */
    @GetMapping("/merchant")
    public R<Map<String, Object>> merchant(@RequestParam Long id) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        List<Product> products = productService.list(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, id)
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreateTime)
                .last("limit 50"));
        productService.fillInfo(products);

        Map<String, Object> data = new HashMap<>(4);
        data.put("merchant", merchant);
        data.put("products", products);
        return R.ok(data);
    }

    /** 上报浏览：targetType 1=产品 2=商户 */
    @PostMapping("/view")
    public R<Void> view(@RequestBody Map<String, Object> body) {
        Integer targetType = Integer.valueOf(body.get("targetType").toString());
        Long targetId = Long.valueOf(body.get("targetId").toString());
        String openid = body.get("openid") == null ? null : body.get("openid").toString();
        statService.recordView(targetType, targetId, openid);
        return R.ok();
    }

    /** 上报转发 */
    @PostMapping("/forward")
    public R<Void> forward(@RequestBody Map<String, Object> body) {
        Integer targetType = Integer.valueOf(body.get("targetType").toString());
        Long targetId = Long.valueOf(body.get("targetId").toString());
        String openid = body.get("openid") == null ? null : body.get("openid").toString();
        statService.recordForward(targetType, targetId, openid);
        return R.ok();
    }

    /** 我的浏览记录（按最近的在前） */
    @GetMapping("/history")
    public R<List<Product>> history(@RequestParam String openid) {
        List<ViewRecord> records = viewRecordMapper.selectList(Wrappers.<ViewRecord>lambdaQuery()
                .eq(ViewRecord::getTargetType, 1)
                .eq(ViewRecord::getOpenid, openid)
                .orderByDesc(ViewRecord::getCreateTime)
                .last("limit 30"));
        List<Long> ids = records.stream().map(ViewRecord::getTargetId).distinct().toList();
        if (ids.isEmpty()) {
            return R.ok(new ArrayList<>());
        }
        List<Product> products = productService.list(Wrappers.<Product>lambdaQuery()
                .in(Product::getId, ids));
        Map<Long, Product> map = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));
        List<Product> result = ids.stream().map(map::get)
                .filter(Objects::nonNull).collect(Collectors.toList());
        productService.fillInfo(result);
        return R.ok(result);
    }
}
