package com.trading.backend;


import com.alibaba.fastjson.JSONObject;
import com.trading.backend.mapper.CouponMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FixErrorDataTests extends CouponServiceApplicationTests {

    @Autowired
    private CouponMapper couponMapper;

    @Test
    public void testFixData() {
        couponMapper.selectAll().forEach(coupon -> {
            try{
                //处理title
                JSONObject object = JSONObject.parseObject(coupon.getTitle());
                JSONObject newObj = new JSONObject();
                object.forEach((key,val) ->{
                    newObj.put(key.replace("_","-"),val);
                });
                coupon.setTitle(newObj.toJSONString());

                //处理desc
                JSONObject descObject = JSONObject.parseObject(coupon.getDescr());
                JSONObject newDescObj = new JSONObject();
                descObject.forEach((key,val) ->{
                    newDescObj.put(key.replace("_","-"),val);
                });
                coupon.setDescr(newDescObj.toJSONString());

                couponMapper.updateByPrimaryKeySelective(coupon);

                System.out.println("== handle ==" +coupon.getId());

            }catch (Exception e){
                System.out.println(e);
            }
        });

    }

}
