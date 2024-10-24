package com.trading.backend.exception;

import lombok.Getter;

import java.text.MessageFormat;

@Getter
public class RemoteCallException extends RuntimeException {

    private static final long serialVersionUID = 471786822421876369L;

    private final ExceptionEnum exceptionEnum;

    private final Integer status;

    public RemoteCallException(Integer status, String cause, ExceptionEnum exceptionEnum) {
        super(format(exceptionEnum, status, cause));
        this.status = status;
        this.exceptionEnum = exceptionEnum;
    }

    private static String format(ExceptionEnum exceptionEnum, Object ... keywords) {
        String reason = exceptionEnum.getReason();
        if (reason.contains("{0}"))
            return MessageFormat.format(reason, keywords);
        return reason;
    }
}