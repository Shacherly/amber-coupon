package com.trading.backend;


import com.trading.backend.losermapper.PfCouponDeliveryDetailMapper;
import com.trading.backend.losermapper.PfCouponMapper;
import com.trading.backend.domain.PfCoupon;
import com.trading.backend.domain.PfCouponDeliveryDetail;
import com.trading.backend.service.DataTransTask;
import com.trading.backend.util.PageContext;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import tk.mybatis.mapper.entity.Example;

@ActiveProfiles("local")
public class tradingSourceTests extends CouponServiceApplicationTests {

    @Autowired(required = false)
    private PfCouponMapper pfCouponMapper;
    @Autowired
    private DataTransTask transferService;
    @Autowired(required = false)
    private PfCouponDeliveryDetailMapper deliveryMapper;


    @Test
    public void testSelect() {
        PfCoupon pfCoupon = pfCouponMapper.selectByPrimaryKey(240L);

        System.out.println();
    }

    @Test
    public void transferCoupon() {
        transferService.transferCoupon();
    }

    @Test
    public void transferCashRule() {
        // transferService.transferCashRule();
        // transferService.transferDeduRule();
        transferService.transferInteRule();
    }

    @Test
    public void transferDeliv() {
        Example example = new Example(PfCouponDeliveryDetail.class);
        PageContext.selectPage(() -> deliveryMapper.selectByExample(example), 2, 10, "id asc");
        transferService.transferDelivery(0);
    }

    @Test
    public void transferReward() {
        transferService.transferDelivReward(null);
    }

}
