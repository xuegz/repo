package com.jiancai.service;

import com.jiancai.common.BusinessException;
import com.jiancai.entity.Favorite;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.mapper.FavoriteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 收藏切换：新增 / 取消 / 参数校验
 */
class FavoriteServiceTest {

    private FavoriteMapper favoriteMapper;
    private ProductService productService;
    private MerchantService merchantService;
    private MemberAccessService memberAccessService;
    private FavoriteService service;

    private static final String OPENID = "openid_test_001";

    @BeforeEach
    void setUp() {
        favoriteMapper = mock(FavoriteMapper.class);
        productService = mock(ProductService.class);
        merchantService = mock(MerchantService.class);
        memberAccessService = mock(MemberAccessService.class);
        service = new FavoriteService(favoriteMapper, productService, merchantService, memberAccessService);
    }

    @Test
    void toggle_insertsNewFavorite() {
        Product product = new Product();
        product.setId(10L);
        product.setStatus(1);
        when(productService.getById(10L)).thenReturn(product);
        when(favoriteMapper.selectOne(any())).thenReturn(null);

        assertTrue(service.toggle(OPENID, 1, 10L));
        verify(favoriteMapper).insert(any(Favorite.class));
        verify(favoriteMapper, never()).deleteById(any(Long.class));
    }

    @Test
    void toggle_removesExistingFavorite() {
        Merchant merchant = new Merchant();
        merchant.setId(20L);
        merchant.setStatus(1);
        when(merchantService.getById(20L)).thenReturn(merchant);

        Favorite exist = new Favorite();
        exist.setId(99L);
        when(favoriteMapper.selectOne(any())).thenReturn(exist);

        assertFalse(service.toggle(OPENID, 2, 20L));
        verify(favoriteMapper).deleteById(99L);
        verify(favoriteMapper, never()).insert(any(Favorite.class));
    }

    @Test
    void toggle_rejectsInvalidTarget() {
        assertThrows(BusinessException.class, () -> service.toggle(OPENID, 3, 10L));
        assertThrows(BusinessException.class, () -> service.toggle(" ", 1, 10L));
        assertThrows(BusinessException.class, () -> service.toggle(OPENID, 1, null));
    }

    @Test
    void toggle_rejectsOfflineProduct() {
        Product product = new Product();
        product.setId(10L);
        product.setStatus(0);
        when(productService.getById(10L)).thenReturn(product);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.toggle(OPENID, 1, 10L));
        assertEquals("产品不存在或已下架", ex.getMessage());
        verify(favoriteMapper, never()).insert(any(Favorite.class));
    }

    @Test
    void check_returnsTrueWhenExists() {
        when(favoriteMapper.selectCount(any())).thenReturn(1L);
        assertTrue(service.check(OPENID, 1, 10L));
        assertThrows(BusinessException.class, () -> service.check(OPENID, 9, 10L));
    }
}
