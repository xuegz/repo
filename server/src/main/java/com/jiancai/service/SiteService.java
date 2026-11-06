package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.config.CacheConfig;
import com.jiancai.entity.Ad;
import com.jiancai.entity.Banner;
import com.jiancai.entity.Notice;
import com.jiancai.mapper.AdMapper;
import com.jiancai.mapper.BannerMapper;
import com.jiancai.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 站点内容业务：首页轮播、广告位、市场公告
 */
@Service
public class SiteService {

    @Autowired
    private BannerMapper bannerMapper;

    @Autowired
    private AdMapper adMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    // ---------------- 轮播 ----------------
    public List<Banner> listBanner() {
        return bannerMapper.selectList(Wrappers.<Banner>lambdaQuery()
                .orderByAsc(Banner::getSort).orderByDesc(Banner::getId));
    }

    /** 小程序首页只展示启用的轮播 */
    @Cacheable(cacheNames = CacheConfig.BANNER, key = "'enabled'")
    public List<Banner> listEnabledBanner() {
        return bannerMapper.selectList(Wrappers.<Banner>lambdaQuery()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort).orderByDesc(Banner::getId));
    }

    @CacheEvict(cacheNames = CacheConfig.BANNER, allEntries = true)
    public void saveBanner(Banner banner) {
        if (banner.getSort() == null) {
            banner.setSort(0);
        }
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        if (banner.getId() == null) {
            bannerMapper.insert(banner);
        } else {
            bannerMapper.updateById(banner);
        }
    }

    @CacheEvict(cacheNames = CacheConfig.BANNER, allEntries = true)
    public void removeBanner(Long id) {
        bannerMapper.deleteById(id);
    }

    // ---------------- 广告位 ----------------
    public List<Ad> listAd() {
        return adMapper.selectList(Wrappers.<Ad>lambdaQuery()
                .orderByAsc(Ad::getSort).orderByDesc(Ad::getId));
    }

    @Cacheable(cacheNames = CacheConfig.AD, key = "#position")
    public List<Ad> listAdByPosition(String position) {
        return adMapper.selectList(Wrappers.<Ad>lambdaQuery()
                .eq(Ad::getPosition, position)
                .eq(Ad::getStatus, 1)
                .orderByAsc(Ad::getSort));
    }

    @CacheEvict(cacheNames = CacheConfig.AD, allEntries = true)
    public void saveAd(Ad ad) {
        if (ad.getSort() == null) {
            ad.setSort(0);
        }
        if (ad.getStatus() == null) {
            ad.setStatus(1);
        }
        if (ad.getId() == null) {
            adMapper.insert(ad);
        } else {
            adMapper.updateById(ad);
        }
    }

    @CacheEvict(cacheNames = CacheConfig.AD, allEntries = true)
    public void removeAd(Long id) {
        adMapper.deleteById(id);
    }

    // ---------------- 市场公告 ----------------
    /** 小程序首页滚动公告：启用中，取前 5 条 */
    @Cacheable(cacheNames = CacheConfig.NOTICE, key = "'enabled'")
    public List<Notice> listEnabledNotice() {
        return noticeMapper.selectList(Wrappers.<Notice>lambdaQuery()
                .eq(Notice::getStatus, 1)
                .orderByDesc(Notice::getSort)
                .orderByDesc(Notice::getCreateTime)
                .last("limit 5"));
    }
}
