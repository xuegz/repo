package com.jiancai.service;

import com.jiancai.common.BusinessException;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.MpMerchantBind;
import com.jiancai.entity.Product;
import com.jiancai.mapper.MpMerchantBindMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 会员权益门禁：绑定商户 + 审核通过 + 会员未过期 三者缺一不可
 */
class MemberAccessServiceTest {

    private MpMerchantBindMapper bindMapper;
    private MerchantService merchantService;
    private MemberAccessService service;

    private static final String OPENID = "openid_test_001";

    @BeforeEach
    void setUp() {
        bindMapper = mock(MpMerchantBindMapper.class);
        merchantService = mock(MerchantService.class);
        service = new MemberAccessService(bindMapper, merchantService);
    }

    private void bindTo(Merchant merchant) {
        MpMerchantBind bind = new MpMerchantBind();
        bind.setOpenid(OPENID);
        bind.setMerchantId(merchant == null ? null : merchant.getId());
        when(bindMapper.selectOne(any())).thenReturn(bind);
        if (merchant != null && merchant.getId() != null) {
            when(merchantService.getById(merchant.getId())).thenReturn(merchant);
        }
    }

    private Merchant approvedMerchant(LocalDateTime expireTime) {
        Merchant merchant = new Merchant();
        merchant.setId(1L);
        merchant.setName("测试门店");
        merchant.setStatus(1);
        merchant.setExpireTime(expireTime);
        return merchant;
    }

    @Test
    void isMember_false_whenOpenidMissing() {
        assertFalse(service.isMember(null));
        assertFalse(service.isMember("   "));
        verifyNoInteractions(bindMapper);
    }

    @Test
    void isMember_false_whenNotBound() {
        when(bindMapper.selectOne(any())).thenReturn(null);
        assertFalse(service.isMember(OPENID));
    }

    @Test
    void isMember_false_whenMerchantOffline() {
        Merchant merchant = approvedMerchant(LocalDateTime.now().plusMonths(6));
        merchant.setStatus(0);
        bindTo(merchant);
        assertFalse(service.isMember(OPENID));
    }

    @Test
    void isMember_false_whenExpired() {
        bindTo(approvedMerchant(LocalDateTime.now().minusDays(1)));
        assertFalse(service.isMember(OPENID));
    }

    @Test
    void isMember_true_whenBoundApprovedAndNotExpired() {
        bindTo(approvedMerchant(LocalDateTime.now().plusDays(1)));
        assertTrue(service.isMember(OPENID));
    }

    @Test
    void requireMember_throws403_forNonMember() {
        when(bindMapper.selectOne(any())).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.requireMember(OPENID, "同款比价"));
        assertEquals(403, ex.getCode());
    }

    @Test
    void status_returnsBindingInfo() {
        LocalDateTime expire = LocalDateTime.now().plusMonths(3);
        bindTo(approvedMerchant(expire));
        Map<String, Object> status = service.status(OPENID);
        assertEquals(true, status.get("bound"));
        assertEquals(true, status.get("memberActive"));
        assertEquals(1L, status.get("merchantId"));
        assertEquals("测试门店", status.get("shopName"));
        assertEquals(expire, status.get("expireTime"));
    }

    @Test
    void maskProduct_hidesContactAndLocation_forNonMember() {
        Product product = new Product();
        product.setMerchantPhone("13800000000");
        product.setMerchantAddress("某某市场 1 排 2 号");
        product.setMerchantLatitude(BigDecimal.valueOf(34.26));
        product.setMerchantLongitude(BigDecimal.valueOf(108.95));

        service.maskProduct(product, false);
        assertNull(product.getMerchantPhone());
        assertNull(product.getMerchantAddress());
        assertNull(product.getMerchantLatitude());
        assertNull(product.getMerchantLongitude());
        assertEquals(false, product.getMemberActive());

        service.maskProduct(product, true);
        assertEquals(true, product.getMemberActive());
    }

    @Test
    void maskMerchant_hidesContactAddressCoordinateDistance_forNonMember() {
        Merchant merchant = new Merchant();
        merchant.setContactName("张三");
        merchant.setPhone("13800000000");
        merchant.setAddress("详细地址");
        merchant.setLatitude(BigDecimal.valueOf(34.26));
        merchant.setLongitude(BigDecimal.valueOf(108.95));
        merchant.setDistance(1.5);

        service.maskMerchant(merchant, false);
        assertNull(merchant.getContactName());
        assertNull(merchant.getPhone());
        assertNull(merchant.getAddress());
        assertNull(merchant.getLatitude());
        assertNull(merchant.getLongitude());
        assertNull(merchant.getDistance());
        assertEquals(false, merchant.getMemberActive());
    }
}
