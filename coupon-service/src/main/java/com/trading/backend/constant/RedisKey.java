package com.trading.backend.constant;

public interface RedisKey {

    /**
     * 资产券返现缓存统一前缀
     */
    String CASH_RETURN = "cashreturn:";


    /**
     * 资产券奖励待发放预加载至redis
     */
    String TO_GRANT_CASH_POOL = CASH_RETURN + "to_grant:pool";


    /**
     * 资产券返现每日金额上限KEY
     */
    String GRANTED_TOTAL_DAILY_PREFIX = CASH_RETURN + "granted:daily_total:";


    /**
     * 用户获得的资产券奖励金额未读缓存，已读会删除
     */
    String POPUP_CASH = CASH_RETURN + "granted:popup_map";


    String GRANTED_CELLING2_ALARMED = CASH_RETURN + "granted:alarmed2";
    String GRANTED_CELLING1_ALARMED = CASH_RETURN + "granted:alarmed1";


    String BUFFERED_COUPONS = "buffered:coupon:map";


    String DISTRIBUTED_PREFIX = "distributed:unique:";

    /**
     * 缓存币价
     */
    String BUFFERED_INDEX_PRICE = "buffered:index:price";
}
