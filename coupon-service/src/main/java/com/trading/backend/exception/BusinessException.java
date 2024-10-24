package com.trading.backend.exception;


import lombok.Getter;

import java.text.MessageFormat;

/**
 * @author ~~ trading.s
 * @date 16:46 09/22/21
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -2937777581295743452L;
    private final ExceptionEnum exceptionEnum;


    /**
     * 主动抛出的不用Throwable， 捕获后再抛出需要Throwable
     * @param exceptionEnum
     */
    public BusinessException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getReason());
        this.exceptionEnum = exceptionEnum;
    }

    public BusinessException(ExceptionEnum exceptionEnum, Object... keywords) {
        super(format(exceptionEnum, keywords));
        this.exceptionEnum = exceptionEnum;
    }

    public BusinessException(String cause, ExceptionEnum exceptionEnum) {
        super(cause);
        this.exceptionEnum = exceptionEnum;
    }

    public BusinessException(Throwable cause, ExceptionEnum exceptionEnum) {
        super(cause);
        this.exceptionEnum = exceptionEnum;
    }

    public BusinessException(String message, Throwable cause, ExceptionEnum exceptionEnum) {
        super(message, cause);
        this.exceptionEnum = exceptionEnum;
    }

    private static String format(ExceptionEnum exceptionEnum, Object ... keywords) {
        String reason = exceptionEnum.getReason();
        // String[] message = new String[keywords.length];
        // int index = 0;
        // for (Object keyword : keywords) {
        //     if (Collection.class.isAssignableFrom(keyword.getClass())) {
        //         message[index++] =
        //     }
        // }
        if (reason.contains("{0}"))
            return MessageFormat.format(reason, keywords);
        return reason;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "exceptionEnum=" + exceptionEnum +
                "} " + super.toString();
    }
}
