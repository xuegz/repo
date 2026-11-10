package com.jiancai.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.common.R;
import com.jiancai.config.CacheConfig;
import com.jiancai.entity.Ad;
import com.jiancai.entity.Banner;
import com.jiancai.entity.Notice;
import com.jiancai.mapper.NoticeMapper;
import com.jiancai.service.SiteService;
import com.jiancai.service.StatService;
import com.jiancai.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 平台端 - 内容管理（首页轮播、广告位、市场公告）与数据总览
 */
@RestController
@RequestMapping("/api/admin")
public class AdminContentController {

    @Autowired
    private SiteService siteService;

    @Autowired
    private StatService statService;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private OperationLogService operationLogService;

    // ---------------- 市场公告 ----------------
    @GetMapping("/notice/list")
    public R<List<Notice>> noticeList() {
        return R.ok(noticeMapper.selectList(Wrappers.<Notice>lambdaQuery()
                .orderByDesc(Notice::getSort)
                .orderByDesc(Notice::getCreateTime)));
    }

    @PostMapping("/notice/save")
    @CacheEvict(cacheNames = CacheConfig.NOTICE, allEntries = true)
    public R<Void> noticeSave(@RequestBody Notice notice) {
        boolean isNew = notice.getId() == null;
        if (isNew) {
            noticeMapper.insert(notice);
        } else {
            noticeMapper.updateById(notice);
        }
        operationLogService.record("content_save", "notice", notice.getId(),
                (isNew ? "新增" : "编辑") + "公告「" + notice.getTitle() + "」");
        return R.ok();
    }

    @PostMapping("/notice/remove/{id}")
    @CacheEvict(cacheNames = CacheConfig.NOTICE, allEntries = true)
    public R<Void> noticeRemove(@PathVariable Long id) {
        Notice notice = noticeMapper.selectById(id);
        noticeMapper.deleteById(id);
        operationLogService.record("content_remove", "notice", id,
                "删除公告「" + (notice == null ? id : notice.getTitle()) + "」");
        return R.ok();
    }

    // ---------------- 首页轮播 ----------------
    @GetMapping("/banner/list")
    public R<List<Banner>> bannerList() {
        return R.ok(siteService.listBanner());
    }

    @PostMapping("/banner/save")
    public R<Void> bannerSave(@RequestBody Banner banner) {
        boolean isNew = banner.getId() == null;
        siteService.saveBanner(banner);
        operationLogService.record("content_save", "banner", banner.getId(),
                (isNew ? "新增" : "编辑") + "轮播「" + banner.getTitle() + "」");
        return R.ok();
    }

    @PostMapping("/banner/remove/{id}")
    public R<Void> bannerRemove(@PathVariable Long id) {
        siteService.removeBanner(id);
        operationLogService.record("content_remove", "banner", id, "删除轮播 #" + id);
        return R.ok();
    }

    // ---------------- 广告位 ----------------
    @GetMapping("/ad/list")
    public R<List<Ad>> adList() {
        return R.ok(siteService.listAd());
    }

    @PostMapping("/ad/save")
    public R<Void> adSave(@RequestBody Ad ad) {
        boolean isNew = ad.getId() == null;
        siteService.saveAd(ad);
        operationLogService.record("content_save", "ad", ad.getId(),
                (isNew ? "新增" : "编辑") + "广告位「" + ad.getPosition() + "」");
        return R.ok();
    }

    @PostMapping("/ad/remove/{id}")
    public R<Void> adRemove(@PathVariable Long id) {
        siteService.removeAd(id);
        operationLogService.record("content_remove", "ad", id, "删除广告位 #" + id);
        return R.ok();
    }

    // ---------------- 数据总览 ----------------
    @GetMapping("/stat/overview")
    public R<Map<String, Object>> overview() {
        return R.ok(statService.overview());
    }

    /** 最近 N 天趋势：浏览/转发/电话/询价/新增用户/新增商户 */
    @GetMapping("/stat/trend")
    public R<List<Map<String, Object>>> trend(@RequestParam(defaultValue = "7") int days) {
        return R.ok(statService.trend(days));
    }
}
