package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.entity.MpUser;
import com.jiancai.mapper.MpUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 小程序用户：登录记录与资料保存
 */
@Service
@RequiredArgsConstructor
public class MpUserService {

    private final MpUserMapper mpUserMapper;

    public MpUser findByOpenid(String openid) {
        return mpUserMapper.selectOne(Wrappers.<MpUser>lambdaQuery()
                .eq(MpUser::getOpenid, openid));
    }

    /** 微信登录成功后记录用户，保证未设置头像昵称的用户也能被统计 */
    public void touchLogin(String openid) {
        if (openid == null || openid.isBlank()) {
            return;
        }
        MpUser user = findByOpenid(openid);
        LocalDateTime now = LocalDateTime.now();
        if (user == null) {
            user = new MpUser();
            user.setOpenid(openid);
            user.setCreateTime(now);
            user.setUpdateTime(now);
            user.setLastLoginTime(now);
            mpUserMapper.insert(user);
        } else {
            user.setLastLoginTime(now);
            user.setUpdateTime(now);
            mpUserMapper.updateById(user);
        }
    }

    /** 平台注册序号：按 id 升序的名次，用于「第 N 位用户」展示；用户不存在返回 0 */
    public long userNo(MpUser user) {
        if (user == null || user.getId() == null) {
            return 0;
        }
        return mpUserMapper.selectCount(Wrappers.<MpUser>lambdaQuery()
                .le(MpUser::getId, user.getId()));
    }

    /** 保存昵称/头像，传 null 表示不修改该字段 */
    public MpUser saveProfile(String openid, String nickname, String avatar) {
        MpUser user = findByOpenid(openid);
        LocalDateTime now = LocalDateTime.now();
        if (user == null) {
            user = new MpUser();
            user.setOpenid(openid);
            user.setNickname(nickname);
            user.setAvatar(avatar);
            user.setCreateTime(now);
            user.setUpdateTime(now);
            user.setLastLoginTime(now);
            mpUserMapper.insert(user);
        } else {
            if (nickname != null) {
                user.setNickname(nickname);
            }
            if (avatar != null) {
                user.setAvatar(avatar);
            }
            user.setUpdateTime(now);
            mpUserMapper.updateById(user);
        }
        return user;
    }
}
