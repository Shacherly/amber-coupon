package com.trading.backend.http.request.earn.opponent;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data @Accessors(chain = true)
public class PositionAcquireParam implements Serializable {
    private static final long serialVersionUID = -1196633009662814464L;

    private List<String> user_ids;

    private List<String> position_ids;

    private List<String> coins;

    private List<String> product_types;

    private List<String> status;

    public boolean allNull() {
        return (coins == null || coins.size() == 0)
                && (status == null || status.size() == 0)
                && (user_ids == null || user_ids.size() == 0)
                && (position_ids == null || position_ids.size() == 0)
                && (product_types == null || product_types.size() == 0);
    }

}
