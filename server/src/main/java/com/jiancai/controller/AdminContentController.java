package com.jiancai.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.common.R;
import com.jiancai.entity.Ad;
import com.jiancai.entity.Banner;
import com.jiancai.entity.Notice;
import com.jiancai.mapper.NoticeMapper;
import com.jiancai.service.SiteService;
import com.jiancai.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // ---------------- 市场公告 ----------------
    @GetMapping("/notice/list")
    public R<List<Notice>> noticeList() {
        return R.ok(noticeMapper.selectList(Wrappers.<Notice>lambdaQuery()
                .orderByDesc(Notice::getSort)
                .orderByDesc(Notice::getCreateTime)));
    }

    @PostMapping("/notice/save")
    public R<Void> noticeSave(@RequestBody Notice notice) {
        if (notice.getId() == null) {
            noticeMapper.insert(notice);
        } else {
            noticeMapper.updateById(notice);
        }
        return R.ok();
    }

    @PostMapping("/notice/remove/{id}")
    public R<Void> noticeRemove(@PathVariable Long id) {
        noticeMapper.deleteById(id);
        return R.ok();
    }

    // ---------------- 首页轮播 ----------------
    @GetMapping("/banner/list")
    public R<List<Banner>> bannerList() {
        return R.ok(siteService.listBanner());
    }

    @PostMapping("/banner/save")
    public R<Void> bannerSave(@RequestBody Banner banner) {
        siteService.saveBanner(banner);
        return R.ok();
    }

    @PostMapping("/banner/remove/{id}")
    public R<Void> bannerRemove(@PathVariable Long id) {
        siteService.removeBanner(id);
        return R.ok();
    }

    // ---------------- 广告位 ----------------
    @GetMapping("/ad/list")
    public R<List<Ad>> adList() {
        return R.ok(siteService.listAd());
    }

    @PostMapping("/ad/save")
    public R<Void> adSave(@RequestBody Ad ad) {
        siteService.saveAd(ad);
        return R.ok();
    }

    @PostMapping("/ad/remove/{id}")
    public R<Void> adRemove(@PathVariable Long id) {
        siteService.removeAd(id);
        return R.ok();
    }

    // ---------------- 数据总览 ----------------
    @GetMapping("/stat/overview")
    public R<Map<String, Object>> overview() {
        return R.ok(statService.overview());
    }
}
