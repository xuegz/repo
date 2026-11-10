package com.jiancai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.Category;
import com.jiancai.entity.Favorite;
import com.jiancai.entity.Inquiry;
import com.jiancai.entity.Market;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Notice;
import com.jiancai.entity.Product;
import com.jiancai.entity.ViewRecord;
import com.jiancai.mapper.FavoriteMapper;
import com.jiancai.mapper.InquiryMapper;
import com.jiancai.mapper.ViewRecordMapper;
import com.jiancai.security.JwtUtil;
import com.jiancai.service.CategoryService;
import com.jiancai.service.MarketService;
import com.jiancai.service.MerchantService;
import com.jiancai.service.MpUserService;
import com.jiancai.service.ProductService;
import com.jiancai.service.SiteService;
import com.jiancai.service.StatService;
import com.jiancai.service.WxLoginService;
import com.jiancai.service.WxSubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
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
    private MarketService marketService;

    @Autowired
    private ViewRecordMapper viewRecordMapper;

    @Autowired
    private InquiryMapper inquiryMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private WxLoginService wxLoginService;

    @Autowired
    private MpUserService mpUserService;

    @Autowired
    private WxSubscribeService wxSubscribeService;

    @Autowired
    private JwtUtil jwtUtil;

    /** 微信登录：小程序 wx.login 的 code 换 openid */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, Object> body) {
        String code = body.get("code") == null ? "" : body.get("code").toString().trim();
        if (code.isEmpty()) {
            throw new BusinessException("缺少微信登录凭证");
        }
        if (code.length() > 128) {
            throw new BusinessException("微信登录凭证不合法");
        }
        Map<String, Object> session = wxLoginService.code2Session(code);
        Object openid = session == null ? null : session.get("openid");
        if (openid == null) {
            throw new BusinessException("微信登录失败，请重试");
        }
        String value = openid.toString();
        mpUserService.touchLogin(value);
        Map<String, Object> result = new HashMap<>(4);
        result.put("openid", value);
        result.put("token", jwtUtil.generateUserToken(value));
        Object unionid = session.get("unionid");
        if (unionid != null) {
            result.put("unionid", unionid);
        }
        return R.ok(result);
    }

    /** 订阅消息配置：前端据此调用 wx.requestSubscribeMessage 获取授权 */
    @GetMapping("/subscribe/config")
    public R<Map<String, Object>> subscribeConfig() {
        return R.ok(wxSubscribeService.publicConfig());
    }

    /** 首页聚合：轮播 + 广告位 + 品类 + 热门市场 + 推荐产品 */
    @GetMapping("/home")
    public R<Map<String, Object>> home() {
        Map<String, Object> data = new HashMap<>(8);
        data.put("banners", siteService.listEnabledBanner());
        data.put("ads", siteService.listAdByPosition("home"));
        data.put("categories", categoryService.listEnabledTop());
        List<Market> markets = marketService.listEnabled(null, null, null, null);
        markets.sort(Comparator.comparingInt(
                (Market m) -> m.getMerchantCount() == null ? 0 : m.getMerchantCount()).reversed());
        data.put("markets", markets.size() > 6 ? new ArrayList<>(markets.subList(0, 6)) : markets);

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
        return R.ok(categoryService.listEnabledTop());
    }

    /** 产品列表（分页 / 关键词 匹配名称+品牌+产地 / 品类 / 市场 / 价格排序） */
    @GetMapping("/products")
    public R<IPage<Product>> products(@RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) String market,
                                      @RequestParam(required = false) String sort) {
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
                return R.ok(new Page<>(pageNum, pageSize));
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
        return R.ok(page);
    }

    /** 产品详情 */
    @GetMapping("/product")
    public R<Product> product(@RequestParam Long id) {
        Product product = productService.getById(id);
        if (product == null || !Integer.valueOf(1).equals(product.getStatus())) {
            throw new BusinessException("产品不存在或已下架");
        }
        productService.fillInfo(List.of(product));
        return R.ok(product);
    }

    /** 商户详情（含在售产品） */
    @GetMapping("/merchant")
    public R<Map<String, Object>> merchant(@RequestParam Long id) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null || !Integer.valueOf(1).equals(merchant.getStatus())) {
            throw new BusinessException("商户不存在或未上线");
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

    /** 入驻商户列表（按关键词/市场/品类筛选，含在售产品数；传 lat/lng + sort=distance 按距离排序） */
    @GetMapping("/merchants")
    public R<List<Merchant>> merchants(@RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String market,
                                       @RequestParam(required = false) Long marketId,
                                       @RequestParam(required = false) Long categoryId,
                                       @RequestParam(required = false) Double lat,
                                       @RequestParam(required = false) Double lng,
                                       @RequestParam(required = false) String sort) {
        var wrapper = Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getStatus, 1)
                .like(keyword != null && !keyword.isBlank(), Merchant::getName, keyword)
                .eq(market != null && !market.isBlank(), Merchant::getMarket, market)
                .eq(marketId != null, Merchant::getMarketId, marketId)
                .in(categoryId != null, Merchant::getCategoryId,
                        categoryId == null ? List.of() : categoryService.subtreeIds(categoryId));
        List<Merchant> list = merchantService.list(wrapper);
        for (Merchant m : list) {
            long count = productService.count(Wrappers.<Product>lambdaQuery()
                    .eq(Product::getMerchantId, m.getId())
                    .eq(Product::getStatus, 1));
            m.setProductCount((int) count);
            if (lat != null && lng != null && m.getLatitude() != null && m.getLongitude() != null) {
                m.setDistance(distanceKm(lat, lng,
                        m.getLatitude().doubleValue(), m.getLongitude().doubleValue()));
            }
        }
        Comparator<Merchant> byHot = Comparator.comparingLong(MpController::hotScore).reversed();
        if ("distance".equals(sort) && lat != null && lng != null) {
            Comparator<Merchant> byDistance = Comparator.comparing(
                    m -> m.getDistance() == null ? Double.MAX_VALUE : m.getDistance());
            list.sort(byDistance.thenComparing(byHot));
        } else {
            // 热度加权排序：浏览 1 分 + 转发 2 分 + 电话 5 分
            list.sort(byHot);
        }
        return R.ok(list);
    }

    private static long hotScore(Merchant m) {
        return (m.getViews() == null ? 0 : m.getViews())
                + (m.getForwards() == null ? 0 : m.getForwards()) * 2L
                + (m.getCalls() == null ? 0 : m.getCalls()) * 5L;
    }

    /** 两点球面距离（公里，保留一位小数） */
    private static double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double dLat = radLat2 - radLat1;
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double km = 6371.0 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(km * 10) / 10.0;
    }

    /** 建材市场列表（含入驻商户数；传 lat/lng + sort=distance 按距离排序） */
    @GetMapping("/markets")
    public R<List<Market>> markets(@RequestParam(required = false) Double lat,
                                   @RequestParam(required = false) Double lng,
                                   @RequestParam(required = false) String sort) {
        return R.ok(marketService.listEnabled(lat, lng, sort, null));
    }

    /** 市场详情：入驻商户与在售产品数 */
    @GetMapping("/market")
    public R<Map<String, Object>> market(@RequestParam Long id,
                                         @RequestParam(required = false) Double lat,
                                         @RequestParam(required = false) Double lng) {
        return R.ok(marketService.detail(id, lat, lng));
    }

    /** 比价搜索：输入产品名或品牌，返回各家报价（按价格从低到高） */
    @GetMapping("/price-compare")
    public R<List<Product>> priceCompare(@RequestParam String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException("请输入要比价的产品名称");
        }
        List<Product> list = productService.list(Wrappers.<Product>lambdaQuery()
                .eq(Product::getStatus, 1)
                .and(w -> w.like(Product::getName, keyword)
                        .or().like(Product::getBrand, keyword))
                .orderByAsc(Product::getPrice)
                .last("limit 50"));
        productService.fillInfo(list);
        return R.ok(list);
    }

    /**
     * 同款比价：与指定产品同名，或同品类+品牌+类型的其他商户报价
     * 匹配优先级：同名 > 同品类+品牌+类型 > 同品类
     */
    @GetMapping("/product/compare")
    public R<List<Product>> productCompare(@RequestParam Long id) {
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
        return R.ok(list);
    }

    /** 上报浏览：targetType 1=产品 2=商户 */
    @PostMapping("/view")
    public R<Void> view(@RequestBody(required = false) Map<String, Object> body) {
        Integer targetType = requiredTargetType(body);
        Long targetId = requiredTargetId(body);
        statService.recordView(targetType, targetId, optionalOpenid(body));
        return R.ok();
    }

    /** 上报转发 */
    @PostMapping("/forward")
    public R<Void> forward(@RequestBody(required = false) Map<String, Object> body) {
        Integer targetType = requiredTargetType(body);
        Long targetId = requiredTargetId(body);
        statService.recordForward(targetType, targetId, optionalOpenid(body));
        return R.ok();
    }

    /** 上报电话联系：targetType 1=产品 2=商户，merchantId 可不传由后端推导 */
    @PostMapping("/call")
    public R<Void> call(@RequestBody(required = false) Map<String, Object> body) {
        Integer targetType = requiredTargetType(body);
        Long targetId = requiredTargetId(body);
        Long merchantId = optionalLong(body, "merchantId");
        statService.recordCall(targetType, targetId, merchantId, optionalOpenid(body));
        return R.ok();
    }

    /** 市场公告（启用中，取前 5 条） */
    @GetMapping("/notices")
    public R<List<Notice>> notices() {
        return R.ok(siteService.listEnabledNotice());
    }

    /** 提交询价线索：客户留资，商户端跟进 */
    @PostMapping("/inquiry")
    public R<Void> inquiry(@RequestBody(required = false) Map<String, Object> body) {
        String contact = body == null || body.get("contact") == null ? null : body.get("contact").toString().trim();
        if (contact == null || contact.isBlank()) {
            throw new BusinessException("请填写联系方式");
        }
        Long productId = optionalLong(body, "productId");
        Long merchantId = optionalLong(body, "merchantId");
        if (merchantId == null && productId != null) {
            Product product = productService.getById(productId);
            merchantId = product == null ? null : product.getMerchantId();
        }
        if (merchantId == null) {
            throw new BusinessException("未找到对应商户");
        }

        Inquiry inquiry = new Inquiry();
        inquiry.setProductId(productId);
        inquiry.setMerchantId(merchantId);
        inquiry.setQuantity(body.get("quantity") == null ? null : body.get("quantity").toString());
        inquiry.setContact(contact);
        inquiry.setRemark(body.get("remark") == null ? null : body.get("remark").toString());
        inquiry.setExpectDate(body.get("expectDate") == null ? null : body.get("expectDate").toString());
        inquiry.setOpenid(optionalOpenid(body));
        inquiry.setStatus(0);
        inquiryMapper.insert(inquiry);
        return R.ok();
    }

    /** 我的询价（含商户报价） */
    @GetMapping("/inquiries")
    public R<List<Inquiry>> myInquiries(@RequestParam String openid,
                                        @RequestParam(required = false) Integer status) {
        List<Inquiry> list = inquiryMapper.selectList(Wrappers.<Inquiry>lambdaQuery()
                .eq(Inquiry::getOpenid, openid)
                .eq(status != null, Inquiry::getStatus, status)
                .orderByDesc(Inquiry::getCreateTime)
                .last("limit 50"));
        fillInquiryInfo(list);
        return R.ok(list);
    }

    private void fillInquiryInfo(List<Inquiry> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<Long> productIds = list.stream().map(Inquiry::getProductId)
                .filter(Objects::nonNull).distinct().toList();
        Map<Long, Product> productMap = productIds.isEmpty() ? Map.of()
                : productService.listByIds(productIds).stream()
                        .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));
        List<Long> merchantIds = list.stream().map(Inquiry::getMerchantId)
                .filter(Objects::nonNull).distinct().toList();
        Map<Long, Merchant> merchantMap = merchantIds.isEmpty() ? Map.of()
                : merchantService.listByIds(merchantIds).stream()
                        .collect(Collectors.toMap(Merchant::getId, m -> m, (a, b) -> a));
        for (Inquiry i : list) {
            Product p = productMap.get(i.getProductId());
            if (p != null) {
                i.setProductName(p.getName());
                List<String> images = ProductService.splitImages(p.getImages());
                i.setProductImage(images.isEmpty() ? null : images.get(0));
            }
            Merchant m = merchantMap.get(i.getMerchantId());
            if (m != null) {
                i.setMerchantName(m.getName());
                i.setMerchantPhone(m.getPhone());
            }
        }
    }

    // ---------------- 收藏 ----------------

    /** 收藏/取消收藏：targetType 1=产品 2=商户，返回最新收藏状态 */
    @PostMapping("/favorite/toggle")
    public R<Map<String, Object>> favoriteToggle(@RequestBody Map<String, Object> body) {
        String openid = body.get("openid") == null ? null : body.get("openid").toString();
        if (openid == null || openid.isBlank()) {
            throw new BusinessException("缺少用户标识");
        }
        Integer targetType = body.get("targetType") == null ? null : Integer.valueOf(body.get("targetType").toString());
        Long targetId = body.get("targetId") == null ? null : Long.valueOf(body.get("targetId").toString());
        if (targetType == null || (!targetType.equals(1) && !targetType.equals(2)) || targetId == null) {
            throw new BusinessException("收藏参数不合法");
        }
        if (targetType == 1) {
            Product p = productService.getById(targetId);
            if (p == null || !Integer.valueOf(1).equals(p.getStatus())) {
                throw new BusinessException("产品不存在或已下架");
            }
        } else {
            Merchant m = merchantService.getById(targetId);
            if (m == null || !Integer.valueOf(1).equals(m.getStatus())) {
                throw new BusinessException("商户不存在或未上线");
            }
        }

        Favorite exist = favoriteMapper.selectOne(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getOpenid, openid)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId));
        boolean favorited;
        if (exist != null) {
            favoriteMapper.deleteById(exist.getId());
            favorited = false;
        } else {
            Favorite favorite = new Favorite();
            favorite.setOpenid(openid);
            favorite.setTargetType(targetType);
            favorite.setTargetId(targetId);
            favoriteMapper.insert(favorite);
            favorited = true;
        }
        Map<String, Object> data = new HashMap<>(2);
        data.put("favorited", favorited);
        return R.ok(data);
    }

    /** 是否已收藏 */
    @GetMapping("/favorite/check")
    public R<Boolean> favoriteCheck(@RequestParam String openid,
                                    @RequestParam Integer targetType,
                                    @RequestParam Long targetId) {
        checkTargetType(targetType);
        return R.ok(favoriteMapper.selectCount(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getOpenid, openid)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId)) > 0);
    }

    /** 我的收藏：targetType 1=产品 2=商户，按收藏时间倒序 */
    @GetMapping("/favorites")
    public R<List<?>> favorites(@RequestParam String openid,
                                @RequestParam Integer targetType) {
        checkTargetType(targetType);
        List<Favorite> favorites = favoriteMapper.selectList(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getOpenid, openid)
                .eq(Favorite::getTargetType, targetType)
                .orderByDesc(Favorite::getCreateTime)
                .last("limit 100"));
        List<Long> ids = favorites.stream().map(Favorite::getTargetId).distinct().toList();
        if (ids.isEmpty()) {
            return R.ok(new ArrayList<>());
        }
        if (targetType == 1) {
            List<Product> products = productService.list(Wrappers.<Product>lambdaQuery()
                    .in(Product::getId, ids)
                    .eq(Product::getStatus, 1));
            Map<Long, Product> map = products.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));
            List<Product> result = ids.stream().map(map::get)
                    .filter(Objects::nonNull).collect(Collectors.toList());
            productService.fillInfo(result);
            return R.ok(result);
        }
        List<Merchant> merchants = merchantService.list(Wrappers.<Merchant>lambdaQuery()
                .in(Merchant::getId, ids)
                .eq(Merchant::getStatus, 1));
        Map<Long, Merchant> map = merchants.stream()
                .collect(Collectors.toMap(Merchant::getId, m -> m, (a, b) -> a));
        List<Merchant> result = ids.stream().map(map::get)
                .filter(Objects::nonNull).collect(Collectors.toList());
        for (Merchant m : result) {
            long count = productService.count(Wrappers.<Product>lambdaQuery()
                    .eq(Product::getMerchantId, m.getId())
                    .eq(Product::getStatus, 1));
            m.setProductCount((int) count);
        }
        return R.ok(result);
    }

    /** 清除我的浏览记录（不影响收藏与询价） */
    @PostMapping("/history/clear")
    public R<Void> clearHistory(@RequestBody Map<String, Object> body) {
        String openid = body.get("openid") == null ? null : body.get("openid").toString();
        if (openid == null || openid.isBlank()) {
            throw new BusinessException("缺少用户标识");
        }
        viewRecordMapper.delete(Wrappers.<ViewRecord>lambdaQuery()
                .eq(ViewRecord::getOpenid, openid)
                .eq(ViewRecord::getTargetType, 1));
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
                .in(Product::getId, ids)
                .eq(Product::getStatus, 1));
        Map<Long, Product> map = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));
        List<Product> result = ids.stream().map(map::get)
                .filter(Objects::nonNull).collect(Collectors.toList());
        productService.fillInfo(result);
        return R.ok(result);
    }

    private Integer requiredTargetType(Map<String, Object> body) {
        Long value = optionalLong(body, "targetType");
        if (value == null) {
            throw new BusinessException(400, "缺少目标类型");
        }
        if (value != 1 && value != 2) {
            throw new BusinessException(400, "目标类型不合法");
        }
        return value.intValue();
    }

    private Long requiredTargetId(Map<String, Object> body) {
        Long id = optionalLong(body, "targetId");
        if (id == null || id <= 0) {
            throw new BusinessException(400, "缺少目标编号");
        }
        return id;
    }

    private Long optionalLong(Map<String, Object> body, String key) {
        Object value = body == null ? null : body.get(key);
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException e) {
            throw new BusinessException(400, "参数格式不正确");
        }
    }

    private String optionalOpenid(Map<String, Object> body) {
        Object value = body == null ? null : body.get("openid");
        if (value == null) {
            return null;
        }
        String openid = value.toString().trim();
        if (openid.isEmpty()) {
            return null;
        }
        if (openid.length() > 64) {
            throw new BusinessException(400, "用户标识不合法");
        }
        return openid;
    }

    private void checkTargetType(Integer targetType) {
        if (targetType == null || (!targetType.equals(1) && !targetType.equals(2))) {
            throw new BusinessException(400, "目标类型不合法");
        }
    }
}
