package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.entity.Ad;
import com.jiancai.entity.Banner;
import com.jiancai.mapper.AdMapper;
import com.jiancai.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 站点内容业务：首页轮播 + 广告位
 */
@Service
public class SiteService {

    @Autowired
    private BannerMapper bannerMapper;

    @Autowired
    private AdMapper adMapper;

    // ---------------- 轮播 ----------------
    public List<Banner> listBanner() {
        return bannerMapper.selectList(Wrappers.<Banner>lambdaQuery()
                .orderByAsc(Banner::getSort).orderByDesc(Banner::getId));
    }

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

    public void removeBanner(Long id) {
        bannerMapper.deleteById(id);
    }

    // ---------------- 广告位 ----------------
    public List<Ad> listAd() {
        return adMapper.selectList(Wrappers.<Ad>lambdaQuery()
                .orderByAsc(Ad::getSort).orderByDesc(Ad::getId));
    }

    public List<Ad> listAdByPosition(String position) {
        return adMapper.selectList(Wrappers.<Ad>lambdaQuery()
                .eq(Ad::getPosition, position)
                .eq(Ad::getStatus, 1)
                .orderByAsc(Ad::getSort));
    }

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

    public void removeAd(Long id) {
        adMapper.deleteById(id);
    }
}
