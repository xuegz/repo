package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.R;
import com.jiancai.entity.Product;
import com.jiancai.service.OperationLogService;
import com.jiancai.service.ProductCsvService;
import com.jiancai.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;

/**
 * 平台端 - 产品管理（审核、下架）
 */
@RestController
@RequestMapping("/api/admin/product")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ProductCsvService productCsvService;

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
        Product product = productService.getById(id);
        productService.audit(id, status, reason);
        String actionText = status == 1 ? "通过/上架" : status == 2 ? "拒绝" : status == 3 ? "下架" : "更新";
        String detail = actionText + "产品「" + (product == null ? id : product.getName()) + "」"
                + (status == 2 && reason != null && !reason.isBlank() ? "，原因：" + reason : "");
        operationLogService.record("product_audit", "product", id, detail);
        return R.ok();
    }

    @PostMapping("/remove/{id}")
    public R<Void> remove(@PathVariable Long id) {
        productService.removeById(id);
        return R.ok();
    }

    /** CSV 批量导入：按商户手机号匹配，校验商户/品类后整批入库，有错误则不导入 */
    @PostMapping("/import")
    public R<Map<String, Object>> importCsv(@RequestParam("file") MultipartFile file) {
        return R.ok(productCsvService.importCsv(file));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) Long categoryId,
                                         @RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) Long merchantId) {
        byte[] data = productCsvService.exportCsv(name, categoryId, status, merchantId);
        return csvResponse(data, "products-" + LocalDate.now() + ".csv");
    }

    private ResponseEntity<byte[]> csvResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(new MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8))
                .body(data);
    }
}
