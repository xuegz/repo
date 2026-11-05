package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.Category;
import com.jiancai.entity.Inquiry;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.mapper.InquiryMapper;
import com.jiancai.security.UserContext;
import com.jiancai.service.CategoryService;
import com.jiancai.service.MarketService;
import com.jiancai.service.MerchantService;
import com.jiancai.service.ProductCsvService;
import com.jiancai.service.ProductService;
import com.jiancai.service.StatService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private InquiryMapper inquiryMapper;

    @Autowired
    private MarketService marketService;

    @Autowired
    private ProductCsvService productCsvService;

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
        // 审核状态、统计数据、会员有效期不允许商户自行修改
        merchant.setStatus(null);
        merchant.setViews(null);
        merchant.setForwards(null);
        merchant.setCalls(null);
        merchant.setExpireTime(null);
        merchant.setCreateTime(null);
        merchant.setUpdateTime(LocalDateTime.now());
        marketService.syncMerchantMarket(merchant);
        merchantService.saveOrUpdate(merchant);
        return R.ok();
    }

    // ---------------- 产品管理 ----------------
    @GetMapping("/product/page")
    public R<IPage<Product>> productPage(@RequestParam(defaultValue = "1") long pageNum,
                                         @RequestParam(defaultValue = "10") long pageSize,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) String sort) {
        return R.ok(productService.pageWithInfo(new Page<>(pageNum, pageSize),
                name, null, status, currentMerchantId(), sort));
    }

    /** 各状态产品数量（在售/待审核/未通过/已下架），用于列表页 Tab 展示 */
    @GetMapping("/product/status-count")
    public R<Map<Integer, Long>> statusCount() {
        List<Product> list = productService.list(Wrappers.<Product>lambdaQuery()
                .eq(Product::getMerchantId, currentMerchantId())
                .select(Product::getStatus));
        return R.ok(list.stream()
                .filter(p -> p.getStatus() != null)
                .collect(Collectors.groupingBy(Product::getStatus, Collectors.counting())));
    }

    @PostMapping("/product/save")
    public R<Void> productSave(@RequestBody Product product) {
        Long merchantId = currentMerchantId();
        if (product.getId() == null) {
            // 新增：进入待审核
            product.setMerchantId(merchantId);
            product.setStatus(0);
            product.setRejectReason(null);
            product.setViews(0);
            product.setForwards(0);
            product.setCalls(0);
        } else {
            Product exist = checkOwner(product.getId(), merchantId);
            // 归属、统计、创建时间以数据库为准，避免越权与计数回退
            product.setMerchantId(merchantId);
            product.setViews(exist.getViews());
            product.setForwards(exist.getForwards());
            product.setCalls(exist.getCalls());
            product.setCreateTime(exist.getCreateTime());
            if (Integer.valueOf(2).equals(exist.getStatus())) {
                // 被拒绝的产品修改后重新进入待审核
                product.setStatus(0);
                product.setRejectReason(null);
            } else {
                product.setStatus(exist.getStatus());
                product.setRejectReason(exist.getRejectReason());
            }
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

    /** 导出本商户商品清单 */
    @GetMapping("/product/export")
    public ResponseEntity<byte[]> exportProducts(@RequestParam(required = false) String name,
                                                 @RequestParam(required = false) Integer status) {
        byte[] data = productCsvService.exportMerchantCsv(currentMerchantId(), name, status);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"my-products-" + LocalDate.now() + ".csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(data);
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

    /** 最近动态：浏览 / 转发 / 电话联系 流水 */
    @GetMapping("/stat/records")
    public R<List<Map<String, Object>>> records(@RequestParam(defaultValue = "20") int limit) {
        return R.ok(statService.merchantRecords(currentMerchantId(), limit));
    }

    @GetMapping("/categories")
    public R<List<Category>> categories() {
        return R.ok(categoryService.listEnabled());
    }

    // ---------------- 询价线索 ----------------
    @GetMapping("/inquiry/page")
    public R<IPage<Inquiry>> inquiryPage(@RequestParam(defaultValue = "1") long pageNum,
                                         @RequestParam(defaultValue = "10") long pageSize,
                                         @RequestParam(required = false) Integer status) {
        IPage<Inquiry> page = inquiryMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<Inquiry>lambdaQuery()
                        .eq(Inquiry::getMerchantId, currentMerchantId())
                        .eq(status != null, Inquiry::getStatus, status)
                        .orderByDesc(Inquiry::getCreateTime));
        List<Long> productIds = page.getRecords().stream()
                .map(Inquiry::getProductId)
                .filter(pid -> pid != null)
                .distinct()
                .toList();
        if (!productIds.isEmpty()) {
            Map<Long, Product> productMap = productService.listByIds(productIds).stream()
                    .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));
            page.getRecords().forEach(i -> {
                Product p = productMap.get(i.getProductId());
                if (p != null) {
                    i.setProductName(p.getName());
                    List<String> images = ProductService.splitImages(p.getImages());
                    i.setProductImage(images.isEmpty() ? null : images.get(0));
                }
            });
        }
        return R.ok(page);
    }

    /** 各状态线索数，用于 tab 角标 */
    @GetMapping("/inquiry/counts")
    public R<Map<String, Long>> inquiryCounts() {
        Long merchantId = currentMerchantId();
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("all", inquiryMapper.selectCount(
                Wrappers.<Inquiry>lambdaQuery().eq(Inquiry::getMerchantId, merchantId)));
        counts.put("pending", inquiryMapper.selectCount(
                Wrappers.<Inquiry>lambdaQuery().eq(Inquiry::getMerchantId, merchantId).eq(Inquiry::getStatus, 0)));
        counts.put("quoted", inquiryMapper.selectCount(
                Wrappers.<Inquiry>lambdaQuery().eq(Inquiry::getMerchantId, merchantId).eq(Inquiry::getStatus, 1)));
        counts.put("follow", inquiryMapper.selectCount(
                Wrappers.<Inquiry>lambdaQuery().eq(Inquiry::getMerchantId, merchantId).eq(Inquiry::getStatus, 2)));
        return R.ok(counts);
    }

    /** 商户报价：价格/总价至少填一项，提交后状态变为已报价 */
    @PostMapping("/inquiry/quote/{id}")
    public R<Void> inquiryQuote(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Inquiry inquiry = inquiryMapper.selectById(id);
        if (inquiry == null || !currentMerchantId().equals(inquiry.getMerchantId())) {
            throw new BusinessException(403, "线索不存在");
        }
        BigDecimal quotePrice = parseDecimal(body.get("quotePrice"), "单价");
        BigDecimal quoteTotal = parseDecimal(body.get("quoteTotal"), "总价");
        if (quotePrice == null && quoteTotal == null) {
            throw new BusinessException("请至少填写单价或总价");
        }
        inquiry.setQuotePrice(quotePrice);
        inquiry.setQuoteTotal(quoteTotal);
        inquiry.setQuoteRemark(body.get("quoteRemark") == null ? null : body.get("quoteRemark").toString());
        inquiry.setQuoteValidUntil(parseDate(body.get("quoteValidUntil")));
        inquiry.setQuoteTime(LocalDateTime.now());
        inquiry.setStatus(1);
        inquiryMapper.updateById(inquiry);
        return R.ok();
    }

    /** 标记线索跟进中 */
    @PostMapping("/inquiry/handle/{id}")
    public R<Void> inquiryHandle(@PathVariable Long id) {
        Inquiry inquiry = inquiryMapper.selectById(id);
        if (inquiry == null || !currentMerchantId().equals(inquiry.getMerchantId())) {
            throw new BusinessException(403, "线索不存在");
        }
        inquiry.setStatus(2);
        inquiryMapper.updateById(inquiry);
        return R.ok();
    }

    /** 删除无效线索 */
    @PostMapping("/inquiry/remove/{id}")
    public R<Void> inquiryRemove(@PathVariable Long id) {
        Inquiry inquiry = inquiryMapper.selectById(id);
        if (inquiry == null || !currentMerchantId().equals(inquiry.getMerchantId())) {
            throw new BusinessException(403, "线索不存在");
        }
        inquiryMapper.deleteById(id);
        return R.ok();
    }

    private BigDecimal parseDecimal(Object value, String label) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        try {
            BigDecimal number = new BigDecimal(value.toString().trim());
            if (number.signum() < 0) {
                throw new BusinessException(label + "不能为负数");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new BusinessException(label + "格式不正确");
        }
    }

    private LocalDate parseDate(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.toString().trim());
        } catch (DateTimeParseException e) {
            throw new BusinessException("报价有效期格式不正确");
        }
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
