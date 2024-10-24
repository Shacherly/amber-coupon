package com.trading.backend.common.enums;


import lombok.Getter;

import java.util.Objects;
import java.util.Set;

/**
 * @author ~~ trading.s
 * @date 14:11 11/09/21
 */
@Getter
public enum EventObjectEnum {

    /**
     * 全部用户
     */
    ENTIRE(0, "ALL"),

    /**
     * 选择指定用户 单标签查询
     */
    CHOSEN(1, "SELECTED_USER"),

    /**
     * 导入用户 单标签查询
     */
    IMPORTED(2, "IMPORTED_USER"),

    /**
     * 限定标签用户 多标签查询
     */
    TAGGED(3, "USER_LABEL"),

    /**
     * 审批限定单个用户
     */
    SINGLE(4, "SINGLE_USER"),


    ;


    private Integer object;

    private String property;

    EventObjectEnum(Integer object, String property) {
        this.object = object;
        this.property = property;
    }

    public static boolean sinleTag(String tagParam) {
        return Objects.equals(CHOSEN.getProperty(), tagParam)
                || Objects.equals(IMPORTED.getProperty(), tagParam);
    }

    public static boolean multiTag(String tagParam) {
        return Objects.equals(TAGGED.getProperty(), tagParam);
    }

    public static boolean entire(String userParam) {
        return Objects.equals(ENTIRE.getProperty(), userParam);
    }

    public static boolean singleUser(String userParam) {
        return Objects.equals(SINGLE.getProperty(), userParam);
    }
}
