package com.jiancai.controller;

import com.jiancai.common.R;
import com.jiancai.security.UserContext;
import com.jiancai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 账号自助操作（平台管理员与商户共用）
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private UserService userService;

    /** 修改当前登录账号密码 */
    @PostMapping("/password")
    public R<Void> changePassword(@RequestBody Map<String, String> body) {
        userService.changePassword(UserContext.getUserId(),
                body.get("oldPassword"), body.get("newPassword"));
        return R.ok();
    }
}
