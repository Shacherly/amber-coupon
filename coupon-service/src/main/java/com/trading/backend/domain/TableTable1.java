package com.trading.backend.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Table(name = "table_table1")
public class TableTable1 implements Serializable {
    @KeySql(useGeneratedKeys = true)
    @Column(name = "id", insertable = false)
    private Integer id;

    @Column(name = "now_time")
    private LocalDateTime nowTime;

    @Column(name = "test_name")
    private String testName;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", nowTime=").append(nowTime);
        sb.append(", testName=").append(testName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}