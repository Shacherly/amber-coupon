package com.trading.backend.pojo;


import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data @Accessors(chain = true)
public class ExpireDO {

    private Long id;

    private LocalDateTime exprTime;
}
