package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiancai.common.R;
import com.jiancai.entity.Product;
import com.jiancai.service.MpProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序产品接口：列表 / 详情 / 比价搜索 / 同款比价（无需登录，会员权益在服务端判定）
 */
@RestController
@RequestMapping("/api/mp")
@RequiredArgsConstructor
public class MpProductController {

    private final MpProductQueryService mpProductQueryService;

    /** 产品列表（分页 / 关键词 / 品类 / 市场 / 价格排序） */
    @GetMapping("/products")
    public R<IPage<Product>> products(@RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) String market,
                                      @RequestParam(required = false) String sort,
                                      @RequestParam(required = false) String openid) {
        return R.ok(mpProductQueryService.pageProducts(pageNum, pageSize, keyword,
                categoryId, market, sort, openid));
    }

    /** 产品详情 */
    @GetMapping("/product")
    public R<Product> product(@RequestParam Long id,
                              @RequestParam(required = false) String openid) {
        return R.ok(mpProductQueryService.detail(id, openid));
    }

    /** 比价搜索：输入产品名或品牌，返回各家报价（按价格从低到高，会员专享） */
    @GetMapping("/price-compare")
    public R<List<Product>> priceCompare(@RequestParam String keyword,
                                         @RequestParam(required = false) String openid) {
        return R.ok(mpProductQueryService.priceCompare(keyword, openid));
    }

    /** 同款比价：同名 > 同品类+品牌+类型 > 同品类（会员专享） */
    @GetMapping("/product/compare")
    public R<List<Product>> productCompare(@RequestParam Long id,
                                           @RequestParam(required = false) String openid) {
        return R.ok(mpProductQueryService.compare(id, openid));
    }
}
