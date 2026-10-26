package com.jiancai;

import com.jiancai.entity.SysUser;
import com.jiancai.service.MerchantService;
import com.jiancai.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 首次启动自动创建默认账号（密码使用 BCrypt 加密，避免脚本里的固定哈希不一致）
 */
@Slf4j
@Component
public class DefaultAccountInitializer implements ApplicationRunner {

    @Resource
    private UserService userService;

    @Resource
    private MerchantService merchantService;

    @Value("${jiancai.init-default-account:true}")
    private boolean enabled;

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }
        if (userService.lambdaQuery().eq(SysUser::getUsername, "admin").count() == 0) {
            userService.createUser("admin", "123456", "平台管理员", "13800000000", 1, null);
            log.info("已初始化默认管理员账号：admin / 123456");
        }
        if (merchantService.getById(1L) != null
                && userService.lambdaQuery().eq(SysUser::getUsername, "merchant").count() == 0) {
            userService.createUser("merchant", "123456", "示例商户", "13800138000", 2, 1L);
            log.info("已初始化默认商户账号：merchant / 123456");
        }
    }
}
