package com.jiancai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.jiancai.service.WxSubscribeService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private WxSubscribeService wxSubscribeService;

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

    /** 邀请同行：邀请码、累计邀请与被邀请商户（会员专享） */
    @GetMapping("/invite")
    public R<Map<String, Object>> invite() {
        Long merchantId = currentMerchantId();
        Merchant merchant = merchantService.getById(merchantId);
        if (merchant == null || merchant.getExpireTime() == null
                || !merchant.getExpireTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(403, "邀请同行是会员专享，请续费后再使用");
        }
        return R.ok(merchantService.inviteOverview(merchantId));
    }

    @PostMapping("/shop/save")
    public R<Void> shopSave(@RequestBody Merchant merchant) {
        String name = merchant.getName() == null ? "" : merchant.getName().trim();
        if (name.isEmpty()) {
            throw new BusinessException("请填写门店名称");
        }
        if (name.length() > 128) {
            throw new BusinessException("门店名称不能超过 128 个字");
        }
        if (merchant.getPhone() != null && merchant.getPhone().isBlank()) {
            throw new BusinessException("请填写联系电话");
        }
        checkLength(merchant.getContactName(), 64, "联系人");
        checkLength(merchant.getPhone(), 20, "联系电话");
        checkLength(merchant.getAddress(), 255, "详细地址");
        checkLength(merchant.getLogo(), 255, "门头照片地址");
        checkLength(merchant.getIntro(), 500, "门店简介");
        checkLength(merchant.getProvince(), 64, "省份");
        checkLength(merchant.getCity(), 64, "城市");
        merchant.setName(name);

        boolean clearLocation = Boolean.TRUE.equals(merchant.getClearLocation());
        BigDecimal latitude = merchant.getLatitude();
        BigDecimal longitude = merchant.getLongitude();
        if (!clearLocation) {
            if ((latitude == null) != (longitude == null)) {
                throw new BusinessException("请同时填写经纬度或清空坐标");
            }
            if (latitude != null && (latitude.doubleValue() < -90 || latitude.doubleValue() > 90)) {
                throw new BusinessException("纬度需在 -90 到 90 之间");
            }
            if (longitude != null && (longitude.doubleValue() < -180 || longitude.doubleValue() > 180)) {
                throw new BusinessException("经度需在 -180 到 180 之间");
            }
        }

        Long merchantId = currentMerchantId();
        merchant.setId(merchantId);
        merchant.setClearLocation(null);
        // 审核状态、统计数据、会员有效期不允许商户自行修改
        merchant.setStatus(null);
        merchant.setViews(null);
        merchant.setForwards(null);
        merchant.setCalls(null);
        merchant.setExpireTime(null);
        // 邀请关系只由入驻申请写入，门店资料不允许修改
        merchant.setInviteCode(null);
        merchant.setInviterId(null);
        merchant.setInviteTime(null);
        merchant.setCreateTime(null);
        merchant.setUpdateTime(LocalDateTime.now());
        marketService.syncMerchantMarket(merchant);
        merchantService.saveOrUpdate(merchant);
        if (clearLocation) {
            // 空值默认不参与更新，需显式清空坐标
            merchantService.update(Wrappers.<Merchant>lambdaUpdate()
                    .eq(Merchant::getId, merchantId)
                    .set(Merchant::getLatitude, null)
                    .set(Merchant::getLongitude, null));
        }
        return R.ok();
    }

    private void checkLength(String value, int max, String label) {
        if (value != null && value.length() > max) {
            throw new BusinessException(label + "不能超过 " + max + " 个字");
        }
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

    /** 单个产品详情（小程序编辑页使用，仅限本商户） */
    @GetMapping("/product/detail/{id}")
    public R<Product> productDetail(@PathVariable Long id) {
        Product product = checkOwner(id, currentMerchantId());
        product.setImageList(ProductService.splitImages(product.getImages()));
        return R.ok(product);
    }

    @PostMapping("/product/save")
    public R<Void> productSave(@RequestBody Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new BusinessException("请填写产品名称");
        }
        if (product.getCategoryId() == null) {
            throw new BusinessException("请选择产品品类");
        }
        if (product.getPrice() != null && product.getPrice().signum() < 0) {
            throw new BusinessException("产品价格不能为负数");
        }
        product.setName(product.getName().trim());
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

    /** 批量上下架：1=上架 3=下架，仅本商户产品；待审核/被拒绝的产品跳过不上架 */
    @PostMapping("/product/batch-status")
    public R<Map<String, Integer>> productBatchStatus(@RequestBody Map<String, Object> body) {
        List<Long> ids = parseIds(body.get("ids"));
        Integer status = Integer.valueOf(body.get("status").toString());
        if (!status.equals(1) && !status.equals(3)) {
            throw new BusinessException("状态不合法");
        }
        Long merchantId = currentMerchantId();
        int updated = 0;
        int skipped = 0;
        for (Long id : ids) {
            Product exist = productService.getById(id);
            if (exist == null || !merchantId.equals(exist.getMerchantId())) {
                skipped++;
                continue;
            }
            if (status.equals(1)
                    && (Integer.valueOf(0).equals(exist.getStatus())
                    || Integer.valueOf(2).equals(exist.getStatus()))) {
                // 未通过审核的产品不能上架
                skipped++;
                continue;
            }
            productService.changeStatus(id, status);
            updated++;
        }
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("updated", updated);
        result.put("skipped", skipped);
        return R.ok(result);
    }

    /** 批量删除本商户产品，返回实际删除数 */
    @PostMapping("/product/batch-remove")
    public R<Integer> productBatchRemove(@RequestBody Map<String, Object> body) {
        List<Long> ids = parseIds(body.get("ids"));
        Long merchantId = currentMerchantId();
        long count = productService.count(Wrappers.<Product>lambdaQuery()
                .in(Product::getId, ids)
                .eq(Product::getMerchantId, merchantId));
        if (count > 0) {
            productService.remove(Wrappers.<Product>lambdaQuery()
                    .in(Product::getId, ids)
                    .eq(Product::getMerchantId, merchantId));
        }
        return R.ok((int) count);
    }

    /** 商户自助导入商品：CSV 品类/名称校验同平台端，但导入后进入待审核（status=0），不绕过审核 */
    @PostMapping("/product/import")
    public R<Map<String, Object>> importProducts(@RequestParam("file") MultipartFile file) {
        return R.ok(productCsvService.importMerchantCsv(file, currentMerchantId()));
    }

    /** 批量操作的 id 列表：支持数组或逗号分隔字符串，1~100 个 */
    private static List<Long> parseIds(Object value) {
        if (value == null) {
            throw new BusinessException("请选择要操作的产品");
        }
        List<String> parts;
        if (value instanceof List<?> list) {
            parts = list.stream().map(String::valueOf).toList();
        } else {
            parts = List.of(value.toString().split(","));
        }
        try {
            List<Long> ids = parts.stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::valueOf)
                    .distinct()
                    .toList();
            if (ids.isEmpty() || ids.size() > 100) {
                throw new BusinessException("每次最多操作 100 个产品");
            }
            return ids;
        } catch (NumberFormatException e) {
            throw new BusinessException("产品 id 不合法");
        }
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

    /** 各状态线索数，用于 tab 角标（单条 GROUP BY，替代 4 次 selectCount） */
    @GetMapping("/inquiry/counts")
    public R<Map<String, Long>> inquiryCounts() {
        Long merchantId = currentMerchantId();
        long all = 0, pending = 0, quoted = 0, follow = 0;
        for (Map<String, Object> row : inquiryMapper.selectMaps(new QueryWrapper<Inquiry>()
                .select("status", "COUNT(*) AS c")
                .eq("merchant_id", merchantId)
                .groupBy("status"))) {
            long c = 0;
            Object count = row.containsKey("c") ? row.get("c") : row.get("C");
            if (count instanceof Number number) {
                c = number.longValue();
            }
            all += c;
            Object status = row.containsKey("status") ? row.get("status") : row.get("STATUS");
            if (status == null) {
                continue;
            }
            switch (Integer.valueOf(status.toString())) {
                case 0 -> pending = c;
                case 1 -> quoted = c;
                case 2 -> follow = c;
                default -> { }
            }
        }
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("all", all);
        counts.put("pending", pending);
        counts.put("quoted", quoted);
        counts.put("follow", follow);
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
        wxSubscribeService.sendQuoteNotice(inquiry);
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
