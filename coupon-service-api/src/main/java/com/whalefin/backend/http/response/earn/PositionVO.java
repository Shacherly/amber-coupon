package com.trading.backend.http.response.earn;


import com.trading.backend.http.request.earn.opponent.CashEarnAcquireParam;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;


/**
 * @author ~~ trading.s
 * @date 12:29 10/15/21
 */
@Data @Accessors(chain = true)
public class PositionVO implements Serializable {

    private static final long serialVersionUID = -1901649170345507218L;

    private String position_id;

    private String user_id;

    private String product_name;

    private String product_type;

    private String position_status;

    private String coin;

    private Integer maturity;

    private String residual_maturity;

    private String subscribe_quantity;

    private String redeem_quantity;

    private String apr;

    private String interest;

    private String hold_principal;

    private String hold_interest;

    private Long created_time;

    private Long active_time;

    private Long expire_time;

    private Long finish_time;

    public boolean eligibleHoldingOrEnded(CashEarnAcquireParam acquireParam) {
        boolean sizeAndCoin = false;

        if (StringUtils.equals("HOLDING", position_status)) {
            for (CashEarnAcquireParam.EarnCoinRule earnCoinRule : acquireParam.getCoin_rule()) {
                if (new BigDecimal(hold_principal)
                        .compareTo(new BigDecimal(earnCoinRule.getHolding_size())) >= 0
                        && StringUtils.equalsIgnoreCase(coin, earnCoinRule.getHolding_coin())) {
                    sizeAndCoin = true;
                    break;
                }
            }
            return maturity >= acquireParam.getDuration() && sizeAndCoin;
        }
        else if (StringUtils.equals("FINISH", position_status)) {
            for (CashEarnAcquireParam.EarnCoinRule earnCoinRule : acquireParam.getCoin_rule()) {
                if (new BigDecimal(subscribe_quantity)
                        .compareTo(new BigDecimal(earnCoinRule.getHolding_size())) >= 0
                        && StringUtils.equalsIgnoreCase(coin, earnCoinRule.getHolding_coin())) {
                    sizeAndCoin = true;
                    break;
                }
            }
            return Duration.ofMillis(finish_time - active_time).toDays() >= acquireParam.getDuration() && sizeAndCoin;
        }
        return false;
    }
}
