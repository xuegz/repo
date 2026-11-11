package com.jiancai.service;

import com.jiancai.entity.MpUser;
import com.jiancai.mapper.MpUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 平台注册序号：用于「我的」页展示「第 N 位用户」
 */
class MpUserServiceTest {

    private MpUserMapper mpUserMapper;
    private MpUserService service;

    @BeforeEach
    void setUp() {
        mpUserMapper = mock(MpUserMapper.class);
        service = new MpUserService(mpUserMapper);
    }

    @Test
    void userNo_zeroWhenUserMissingOrUnsaved() {
        assertEquals(0, service.userNo(null));
        assertEquals(0, service.userNo(new MpUser()));
        verifyNoInteractions(mpUserMapper);
    }

    @Test
    void userNo_countsUsersUpToCurrentId() {
        MpUser user = new MpUser();
        user.setId(7L);
        when(mpUserMapper.selectCount(any())).thenReturn(7L);

        assertEquals(7, service.userNo(user));
        verify(mpUserMapper).selectCount(any());
    }
}
