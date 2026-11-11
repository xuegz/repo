package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Favorite;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 小程序收藏：产品 / 商户收藏（同一访客对同一对象唯一）
 */
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductService productService;
    private final MerchantService merchantService;
    private final MemberAccessService memberAccessService;

    /** 收藏/取消收藏，返回最新收藏状态 */
    @Transactional
    public boolean toggle(String openid, Integer targetType, Long targetId) {
        if (openid == null || openid.isBlank()) {
            throw new BusinessException("缺少用户标识");
        }
        checkTargetType(targetType);
        if (targetId == null) {
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
        if (exist != null) {
            favoriteMapper.deleteById(exist.getId());
            return false;
        }
        Favorite favorite = new Favorite();
        favorite.setOpenid(openid);
        favorite.setTargetType(targetType);
        favorite.setTargetId(targetId);
        favoriteMapper.insert(favorite);
        return true;
    }

    /** 是否已收藏 */
    public boolean check(String openid, Integer targetType, Long targetId) {
        checkTargetType(targetType);
        return favoriteMapper.selectCount(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getOpenid, openid)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId)) > 0;
    }

    /** 我的收藏：targetType 1=产品 2=商户，按收藏时间倒序（非会员隐藏联系方式） */
    public List<?> list(String openid, Integer targetType) {
        checkTargetType(targetType);
        List<Favorite> favorites = favoriteMapper.selectList(Wrappers.<Favorite>lambdaQuery()
                .eq(Favorite::getOpenid, openid)
                .eq(Favorite::getTargetType, targetType)
                .orderByDesc(Favorite::getCreateTime)
                .last("limit 100"));
        List<Long> ids = favorites.stream().map(Favorite::getTargetId).distinct().toList();
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        boolean member = memberAccessService.isMember(openid);
        if (targetType == 1) {
            List<Product> products = productService.list(Wrappers.<Product>lambdaQuery()
                    .in(Product::getId, ids)
                    .eq(Product::getStatus, 1));
            Map<Long, Product> map = products.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));
            List<Product> result = ids.stream().map(map::get)
                    .filter(Objects::nonNull).collect(Collectors.toList());
            productService.fillInfo(result);
            memberAccessService.maskProducts(result, member);
            return result;
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
        memberAccessService.maskMerchants(result, member);
        return result;
    }

    private static void checkTargetType(Integer targetType) {
        if (targetType == null || (!targetType.equals(1) && !targetType.equals(2))) {
            throw new BusinessException(400, "目标类型不合法");
        }
    }
}
