package com.trading.backend.http.request.earn.opponent;


import cn.hutool.core.collection.CollectionUtil;
import com.trading.backend.domain.CashCouponRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author ~~ trading.s
 * @date 12:14 10/16/21
 */
@Data @Accessors(chain = true)
@NoArgsConstructor @AllArgsConstructor
public class CashEarnAcquireParam implements Serializable {
    private static final long serialVersionUID = -4670026314983501556L;

    private String user_id;

    private Integer duration;

    private List<EarnCoinRule> coin_rule;

    @Data @Accessors(chain = true)
    @AllArgsConstructor @NoArgsConstructor
    public static class EarnCoinRule implements Serializable{
        private static final long serialVersionUID = -8092272995410484443L;

        private String holding_coin;

        private String holding_size;
    }

    public boolean hasAnyNull() {
        return Objects.isNull(duration) || StringUtils.isBlank(user_id) || CollectionUtil.isEmpty(coin_rule);
    }

    public static CashEarnAcquireParam fromCashCouponRule(String uid, CashCouponRule rule) {
        List<EarnCoinRule> collect = rule.getCoinRules().stream().map(val -> new EarnCoinRule(val.getApplyCoin(), val.getMinAmount().toPlainString())).collect(Collectors.toList());
        return new CashEarnAcquireParam(uid, rule.getMinSubscrDays(), collect);
    }
}
