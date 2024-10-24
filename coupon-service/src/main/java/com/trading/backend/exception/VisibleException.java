package com.trading.backend.exception;


import lombok.Getter;

import java.text.MessageFormat;

@Getter
public class VisibleException extends RuntimeException {
    private static final long serialVersionUID = 3363863272647538821L;

    private final ExceptionEnum exceptionEnum;

    public VisibleException(ExceptionEnum exceptionEnum, Object... keywords) {
        super(format(exceptionEnum, keywords));
        this.exceptionEnum = exceptionEnum;
    }


    private static String format(ExceptionEnum exceptionEnum, Object ... keywords) {
        String reason = exceptionEnum.getReason();
        if (reason.contains("{0}"))
            return MessageFormat.format(reason, keywords);
        return reason;
    }
}
