package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.entity.SysConfig;
import com.jiancai.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 系统配置（键值对）
 */
@Service
@RequiredArgsConstructor
public class SystemConfigService {

    public static final String MEMBERSHIP_MONTHS = "membership_months";

    private static final int DEFAULT_MEMBERSHIP_MONTHS = 12;

    private final SysConfigMapper sysConfigMapper;

    /** 推广期赠送会员的有效期月数 */
    public int membershipMonths() {
        SysConfig config = findByKey(MEMBERSHIP_MONTHS);
        if (config == null) {
            return DEFAULT_MEMBERSHIP_MONTHS;
        }
        try {
            int months = Integer.parseInt(config.getConfigValue());
            return months > 0 ? months : DEFAULT_MEMBERSHIP_MONTHS;
        } catch (NumberFormatException e) {
            return DEFAULT_MEMBERSHIP_MONTHS;
        }
    }

    public void saveMembershipMonths(int months) {
        SysConfig config = findByKey(MEMBERSHIP_MONTHS);
        if (config == null) {
            config = new SysConfig();
            config.setConfigKey(MEMBERSHIP_MONTHS);
            config.setConfigValue(String.valueOf(months));
            config.setRemark("推广期入驻赠送会员的有效期（月）");
            config.setUpdateTime(LocalDateTime.now());
            sysConfigMapper.insert(config);
            return;
        }
        config.setConfigValue(String.valueOf(months));
        config.setUpdateTime(LocalDateTime.now());
        sysConfigMapper.updateById(config);
    }

    private SysConfig findByKey(String key) {
        return sysConfigMapper.selectOne(Wrappers.<SysConfig>lambdaQuery()
                .eq(SysConfig::getConfigKey, key));
    }
}
