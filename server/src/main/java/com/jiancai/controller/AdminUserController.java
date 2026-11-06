package com.jiancai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.BusinessException;
import com.jiancai.common.R;
import com.jiancai.entity.MpUser;
import com.jiancai.mapper.MpUserMapper;
import com.jiancai.service.MpUserAdminService;
import com.jiancai.service.WxSubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 平台端 - 小程序用户管理
 */
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final MpUserMapper mpUserMapper;
    private final WxSubscribeService wxSubscribeService;
    private final MpUserAdminService mpUserAdminService;

    /** 用户列表，支持按昵称 / openid 搜索 */
    @GetMapping("/page")
    public R<IPage<MpUser>> page(@RequestParam(defaultValue = "1") long pageNum,
                                 @RequestParam(defaultValue = "10") long pageSize,
                                 @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<MpUser> query = Wrappers.lambdaQuery();
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            query.and(w -> w.like(MpUser::getNickname, kw)
                    .or().like(MpUser::getOpenid, kw));
        }
        query.orderByDesc(MpUser::getLastLoginTime)
                .orderByDesc(MpUser::getCreateTime);
        return R.ok(mpUserMapper.selectPage(new Page<>(pageNum, pageSize), query));
    }

    /** 用户概览：总数、今日新增、今日活跃、近 7 日活跃 */
    @GetMapping("/overview")
    public R<Map<String, Object>> overview() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        Map<String, Object> map = new HashMap<>(8);
        map.put("userTotal", mpUserMapper.selectCount(null));
        map.put("userToday", mpUserMapper.selectCount(Wrappers.<MpUser>lambdaQuery()
                .ge(MpUser::getCreateTime, todayStart)));
        map.put("userActiveToday", mpUserMapper.selectCount(Wrappers.<MpUser>lambdaQuery()
                .ge(MpUser::getLastLoginTime, todayStart)));
        map.put("userActive7d", mpUserMapper.selectCount(Wrappers.<MpUser>lambdaQuery()
                .ge(MpUser::getLastLoginTime, weekAgo)));
        return R.ok(map);
    }

    /** 用户详情：基本信息 + 行为统计 + 最近浏览/收藏/电话/询价 */
    @GetMapping("/detail")
    public R<Map<String, Object>> detail(@RequestParam String openid) {
        return R.ok(mpUserAdminService.detail(openid));
    }

    /** 保存平台备注 */
    @PostMapping("/remark")
    public R<Void> remark(@RequestBody Map<String, String> body) {
        mpUserAdminService.saveRemark(body.get("openid"), body.get("remark"));
        return R.ok();
    }

    /** 导出用户清单（最多 5000 条） */
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(required = false) String keyword) {
        byte[] data = mpUserAdminService.exportCsv(keyword);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users-" + LocalDate.now() + ".csv\"")
                .contentType(new MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8))
                .body(data);
    }

    /** 给指定用户发一条测试订阅消息，用于验证模板配置 */
    @PostMapping("/subscribe/test")
    public R<Void> testSubscribe(@RequestBody Map<String, String> body) {
        String openid = body == null ? null : body.get("openid");
        if (openid == null || openid.isBlank()) {
            throw new BusinessException("请填写 openid");
        }
        String target = openid.trim();
        Long count = mpUserMapper.selectCount(Wrappers.<MpUser>lambdaQuery()
                .eq(MpUser::getOpenid, target));
        if (count == null || count == 0) {
            throw new BusinessException("用户不存在");
        }
        wxSubscribeService.sendTest(target);
        return R.ok();
    }
}
