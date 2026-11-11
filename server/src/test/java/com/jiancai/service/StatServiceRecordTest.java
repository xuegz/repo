package com.jiancai.service;

import com.jiancai.entity.CallRecord;
import com.jiancai.entity.Product;
import com.jiancai.entity.ViewRecord;
import com.jiancai.mapper.CallRecordMapper;
import com.jiancai.mapper.ForwardRecordMapper;
import com.jiancai.mapper.InquiryMapper;
import com.jiancai.mapper.MerchantMapper;
import com.jiancai.mapper.MpUserMapper;
import com.jiancai.mapper.ProductMapper;
import com.jiancai.mapper.ViewRecordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 行为上报：流水落库与计数器累加必须成对发生（由 @Transactional 保证原子性）
 */
class StatServiceRecordTest {

    private ViewRecordMapper viewRecordMapper;
    private ForwardRecordMapper forwardRecordMapper;
    private CallRecordMapper callRecordMapper;
    private ProductMapper productMapper;
    private MerchantMapper merchantMapper;
    private StatService service;

    @BeforeEach
    void setUp() {
        viewRecordMapper = mock(ViewRecordMapper.class);
        forwardRecordMapper = mock(ForwardRecordMapper.class);
        callRecordMapper = mock(CallRecordMapper.class);
        productMapper = mock(ProductMapper.class);
        merchantMapper = mock(MerchantMapper.class);
        service = new StatService();
        ReflectionTestUtils.setField(service, "viewRecordMapper", viewRecordMapper);
        ReflectionTestUtils.setField(service, "forwardRecordMapper", forwardRecordMapper);
        ReflectionTestUtils.setField(service, "callRecordMapper", callRecordMapper);
        ReflectionTestUtils.setField(service, "productMapper", productMapper);
        ReflectionTestUtils.setField(service, "merchantMapper", merchantMapper);
        ReflectionTestUtils.setField(service, "mpUserMapper", mock(MpUserMapper.class));
        ReflectionTestUtils.setField(service, "inquiryMapper", mock(InquiryMapper.class));
    }

    @Test
    void recordView_insertsRecordAndIncrementsProductCounter() {
        service.recordView(1, 10L, "openid_x");
        verify(viewRecordMapper).insert(any(ViewRecord.class));
        verify(productMapper).update(isNull(), any());
        verifyNoInteractions(merchantMapper);
    }

    @Test
    void recordView_merchantTargetIncrementsMerchantCounter() {
        service.recordView(2, 20L, null);
        verify(viewRecordMapper).insert(any(ViewRecord.class));
        verify(merchantMapper).update(isNull(), any());
        verifyNoInteractions(productMapper);
    }

    @Test
    void recordCall_derivesMerchantFromProduct() {
        Product product = new Product();
        product.setId(10L);
        product.setMerchantId(88L);
        when(productMapper.selectById(10L)).thenReturn(product);

        service.recordCall(1, 10L, null, "openid_x");

        verify(callRecordMapper).insert(argThat((CallRecord r) -> Long.valueOf(88L).equals(r.getMerchantId())));
        verify(productMapper).update(isNull(), any());
    }

    @Test
    void recordCall_skipsWhenMerchantCannotBeDerived() {
        when(productMapper.selectById(10L)).thenReturn(null);
        service.recordCall(1, 10L, null, null);
        verifyNoInteractions(callRecordMapper);
    }
}
