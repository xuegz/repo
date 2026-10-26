package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.Category;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.security.UserContext;
import com.jiancai.service.CategoryService;
import com.jiancai.service.MerchantService;
import com.jiancai.service.ProductService;
import com.jiancai.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商户端 - 门店信息、产品管理、数据看板
 */
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ProductService productService;

    @Autowired
    private StatService statService;

    @Autowired
    private CategoryService categoryService;

    private Long currentMerchantId() {
        Long merchantId = UserContext.getMerchantId();
        if (merchantId == null) {
            throw new BusinessException(403, "当前账号未关联商户");
        }
        return merchantId;
    }

    // ---------------- 门店信息 ----------------
    @GetMapping("/shop/info")
    public R<Merchant> shopInfo() {
        return R.ok(merchantService.getById(currentMerchantId()));
    }

    @PostMapping("/shop/save")
    public R<Void> shopSave(@RequestBody Merchant merchant) {
        merchant.setId(currentMerchantId());
        // 审核状态、统计数据不允许商户自行修改
        merchant.setStatus(null);
        merchant.setViews(null);
        merchant.setForwards(null);
        merchantService.saveOrUpdate(merchant);
        return R.ok();
    }

    // ---------------- 产品管理 ----------------
    @GetMapping("/product/page")
    public R<IPage<Product>> productPage(@RequestParam(defaultValue = "1") long pageNum,
                                         @RequestParam(defaultValue = "10") long pageSize,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) Integer status) {
        return R.ok(productService.pageWithInfo(new Page<>(pageNum, pageSize),
                name, null, status, currentMerchantId()));
    }

    @PostMapping("/product/save")
    public R<Void> productSave(@RequestBody Product product) {
        Long merchantId = currentMerchantId();
        if (product.getId() == null) {
            // 新增：进入待审核
            product.setMerchantId(merchantId);
            product.setStatus(0);
        } else {
            checkOwner(product.getId(), merchantId);
        }
        productService.prepareSave(product);
        productService.saveOrUpdate(product);
        return R.ok();
    }

    /** 上下架：1=上架 3=下架 */
    @PostMapping("/product/status")
    public R<Void> productStatus(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        Integer status = Integer.valueOf(body.get("status").toString());
        if (!status.equals(1) && !status.equals(3)) {
            throw new BusinessException("状态不合法");
        }
        Long merchantId = currentMerchantId();
        Product exist = checkOwner(id, merchantId);
        if (Integer.valueOf(0).equals(exist.getStatus()) || Integer.valueOf(2).equals(exist.getStatus())) {
            throw new BusinessException("产品尚未通过审核，无法上架");
        }
        productService.changeStatus(id, status);
        return R.ok();
    }

    @PostMapping("/product/remove/{id}")
    public R<Void> productRemove(@PathVariable Long id) {
        checkOwner(id, currentMerchantId());
        productService.removeById(id);
        return R.ok();
    }

    // ---------------- 数据看板 ----------------
    @GetMapping("/stat/overview")
    public R<Map<String, Object>> overview() {
        return R.ok(statService.merchantOverview(currentMerchantId()));
    }

    @GetMapping("/stat/hot")
    public R<List<Product>> hot() {
        return R.ok(statService.merchantHotProduct(currentMerchantId()));
    }

    @GetMapping("/categories")
    public R<List<Category>> categories() {
        return R.ok(categoryService.listEnabled());
    }

    /** 校验产品归属当前商户 */
    private Product checkOwner(Long productId, Long merchantId) {
        Product exist = productService.getById(productId);
        if (exist == null) {
            throw new BusinessException("产品不存在");
        }
        if (!merchantId.equals(exist.getMerchantId())) {
            throw new BusinessException(403, "只能操作自己的产品");
        }
        return exist;
    }
}
