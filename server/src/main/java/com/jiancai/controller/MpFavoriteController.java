package com.jiancai.controller;

import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序收藏接口：产品 / 商户收藏（无需登录，按 openid 识别）
 */
@RestController
@RequestMapping("/api/mp")
@RequiredArgsConstructor
public class MpFavoriteController {

    private final FavoriteService favoriteService;

    /** 收藏/取消收藏：targetType 1=产品 2=商户，返回最新收藏状态 */
    @PostMapping("/favorite/toggle")
    public R<Map<String, Object>> favoriteToggle(@RequestBody Map<String, Object> body) {
        String openid = text(body.get("openid"));
        Integer targetType = body.get("targetType") == null
                ? null : Integer.valueOf(body.get("targetType").toString());
        Long targetId = body.get("targetId") == null
                ? null : Long.valueOf(body.get("targetId").toString());
        Map<String, Object> data = new HashMap<>(2);
        data.put("favorited", favoriteService.toggle(openid, targetType, targetId));
        return R.ok(data);
    }

    /** 是否已收藏 */
    @GetMapping("/favorite/check")
    public R<Boolean> favoriteCheck(@RequestParam String openid,
                                    @RequestParam Integer targetType,
                                    @RequestParam Long targetId) {
        return R.ok(favoriteService.check(openid, targetType, targetId));
    }

    /** 我的收藏：targetType 1=产品 2=商户，按收藏时间倒序 */
    @GetMapping("/favorites")
    public R<List<?>> favorites(@RequestParam String openid,
                                @RequestParam Integer targetType) {
        return R.ok(favoriteService.list(openid, targetType));
    }

    private static String text(Object value) {
        if (value == null) {
            return null;
        }
        String s = value.toString().trim();
        if (s.length() > 64) {
            throw new BusinessException(400, "用户标识不合法");
        }
        return s;
    }
}
