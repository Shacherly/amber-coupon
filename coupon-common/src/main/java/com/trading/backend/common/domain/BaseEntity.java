package com.trading.backend.common.domain;

import lombok.Getter;
import lombok.Setter;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ~~ trading mu
 * @date 16:37 03/28/22
 */
@Setter @Getter
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 7559299825901625522L;

    @Id
    @KeySql(useGeneratedKeys = true)
    @Column(name = "id", insertable = false)
    private Long id;

    @Column(name = "ctime")
    private LocalDateTime ctime;

    @Column(name = "utime")
    private LocalDateTime utime;

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                ", ctime=" + ctime +
                ", utime=" + utime +
                '}';
    }
}
