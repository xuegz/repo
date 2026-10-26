package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.R;
import com.jiancai.entity.Product;
import com.jiancai.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 平台端 - 产品管理（审核、下架）
 */
@RestController
@RequestMapping("/api/admin/product")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/page")
    public R<IPage<Product>> page(@RequestParam(defaultValue = "1") long pageNum,
                                  @RequestParam(defaultValue = "10") long pageSize,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) Long merchantId) {
        return R.ok(productService.pageWithInfo(new Page<>(pageNum, pageSize),
                name, categoryId, status, merchantId));
    }

    @GetMapping("/detail")
    public R<Product> detail(@RequestParam Long id) {
        return R.ok(productService.getDetail(id));
    }

    /** 审核：1=通过 2=拒绝（需填原因） 3=下架 */
    @PostMapping("/audit")
    public R<Void> audit(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        Integer status = Integer.valueOf(body.get("status").toString());
        String reason = body.get("reason") == null ? null : body.get("reason").toString();
        productService.audit(id, status, reason);
        return R.ok();
    }

    @PostMapping("/remove/{id}")
    public R<Void> remove(@PathVariable Long id) {
        productService.removeById(id);
        return R.ok();
    }
}
