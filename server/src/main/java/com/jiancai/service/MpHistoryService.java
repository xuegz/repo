package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Product;
import com.jiancai.entity.ViewRecord;
import com.jiancai.mapper.ViewRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 小程序浏览记录：产品浏览历史查询与清除
 */
@Service
@RequiredArgsConstructor
public class MpHistoryService {

    private final ViewRecordMapper viewRecordMapper;
    private final ProductService productService;
    private final MemberAccessService memberAccessService;

    /** 我的浏览记录（按最近的在前；非会员隐藏商户联系方式） */
    public List<Product> list(String openid) {
        List<ViewRecord> records = viewRecordMapper.selectList(Wrappers.<ViewRecord>lambdaQuery()
                .eq(ViewRecord::getTargetType, 1)
                .eq(ViewRecord::getOpenid, openid)
                .orderByDesc(ViewRecord::getCreateTime)
                .last("limit 30"));
        List<Long> ids = records.stream().map(ViewRecord::getTargetId).distinct().toList();
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<Product> products = productService.list(Wrappers.<Product>lambdaQuery()
                .in(Product::getId, ids)
                .eq(Product::getStatus, 1));
        Map<Long, Product> map = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));
        List<Product> result = ids.stream().map(map::get)
                .filter(Objects::nonNull).collect(Collectors.toList());
        productService.fillInfo(result);
        memberAccessService.maskProducts(result, memberAccessService.isMember(openid));
        return result;
    }

    /** 清除我的浏览记录（不影响收藏与询价） */
    public void clear(String openid) {
        if (openid == null || openid.isBlank()) {
            throw new BusinessException("缺少用户标识");
        }
        viewRecordMapper.delete(Wrappers.<ViewRecord>lambdaQuery()
                .eq(ViewRecord::getOpenid, openid)
                .eq(ViewRecord::getTargetType, 1));
    }
}
