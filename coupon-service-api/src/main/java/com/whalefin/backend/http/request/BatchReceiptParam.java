package com.trading.backend.http.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;


/**
 * @author ~~ trading.s
 * @date 15:43 10/26/21
 */
@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BatchReceiptParam implements Serializable {
    private static final long serialVersionUID = 6944368390580751637L;

    @NotEmpty
    @ApiModelProperty(value = "用户id数组")
    private List<String> uids;

    @NotEmpty
    @ApiModelProperty(value = "领取的券id数组")
    private List<Long> couponIds;

    @Override
    public String toString() {
        return "BatchReceiptParam{" +
                "uids=" + uids +
                ", couponIds=" + couponIds +
                '}';
    }
}
