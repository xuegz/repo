package com.jiancai.controller;

import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.Merchant;
import com.jiancai.service.MarketService;
import com.jiancai.service.MerchantService;
import com.jiancai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 登录 / 商户入驻（无需鉴权）
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MarketService marketService;

    /** 平台端与商户端共用登录入口 */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BusinessException("请输入账号和密码");
        }
        return R.ok(userService.login(username, password));
    }

    /** 商户入驻申请（提交后进入待审核，审核通过自动开通账号） */
    @PostMapping("/register")
    public R<Void> register(@RequestBody Merchant merchant) {
        if (merchant.getName() == null || merchant.getName().isBlank()) {
            throw new BusinessException("请填写商户名称");
        }
        if (merchant.getPhone() == null || merchant.getPhone().isBlank()) {
            throw new BusinessException("请填写联系电话");
        }
        merchant.setStatus(0);
        merchant.setViews(0);
        merchant.setForwards(0);
        merchant.setCalls(0);
        marketService.syncMerchantMarket(merchant);
        merchantService.save(merchant);
        return R.ok();
    }
}
