package com.trading.backend.exception;

import com.trading.backend.http.Response;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ValidationException;
import java.text.MessageFormat;
import java.util.StringJoiner;


/**
 * @author ~~ trading.s
 * @date 17:00 09/22/21
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    public Response<Void> handleBusinessException(BusinessException exception) {
        ExceptionEnum expEnum = exception.getExceptionEnum();
        log.error(exception.getMessage(), exception);
        /**
         * expEnum.getReason()已经构造进exception中去的
         */
        Sentry.captureException(exception);
        return Response.fail(expEnum.getCode(), desensitization(expEnum.getReason()));
    }

    /**
     * 明显的可见异常
     * @param exception
     * @return
     */
    @ExceptionHandler(VisibleException.class)
    public Response<Void> handleVisibleException(VisibleException exception) {
        ExceptionEnum expEnum = exception.getExceptionEnum();
        log.error(exception.getMessage(), exception);
        Sentry.captureException(exception);
        return Response.fail(expEnum.getCode(), exception.getMessage());
    }

    @ExceptionHandler(RemoteCallException.class)
    public Response<Void> handleemoteCallException(RemoteCallException exception) {
        ExceptionEnum expEnum = exception.getExceptionEnum();
        log.error(exception.getMessage(), exception);
        Sentry.captureException(exception);
        return Response.fail(expEnum.getCode(), exception.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public Response<Void> handleBindException(BindException ex) {
        StringJoiner sj = new StringJoiner(";");
        ex.getBindingResult().getFieldErrors().forEach(x -> sj.add(x.getField() + " " + x.getDefaultMessage()));
        log.error("bind err, target = {}, msg = {}", ex.getTarget(), sj);
        Sentry.captureException(ex);
        return Response.fail(ExceptionEnum.ARGUMENT_UNVALID.getCode(), sj.toString());
    }

    @ExceptionHandler(ValidationException.class)
    public Response<Void> handleValidationException(ValidationException ex) {
        log.error("valid err, msg = {}", ex.getMessage(), ex);
        Sentry.captureException(ex);
        return Response.fail(ExceptionEnum.ARGUMENT_UNVALID.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringJoiner sj = new StringJoiner(";");
        ex.getBindingResult().getFieldErrors().forEach(x -> sj.add(x.getField() + " " + x.getDefaultMessage()));
        log.error("UnvalidArgument error, parameter = {}, msg = {}", ex.getParameter(), sj);
        Sentry.captureException(ex);
        return Response.fail(ExceptionEnum.ARGUMENT_UNVALID.getCode(), sj.toString());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error("method valid err, msg = {}", ex.getMessage());
        Sentry.captureException(ex);
        return Response.fail(ExceptionEnum.ARGUMENT_UNVALID.getCode(), ex.getMessage());
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response<Void> handleMissingServletRequestBodyrException(HttpMessageNotReadableException ex) {
        log.error("method valid err, msg = {}", ex.getMessage());
        Sentry.captureException(ex);
        return Response.fail(ExceptionEnum.ARGUMENT_UNVALID.getCode(), "Required request body is missing");
    }


    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public Response<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("method not support err, msg = {}", ex.getMessage());
        Sentry.captureException(ex);
        return Response.fail(ExceptionEnum.ARGUMENT_UNVALID.getCode(), ex.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public Response<Void> handleException(Exception ex) {
        log.error("Unexpected Exception, cause {}", ex.getMessage(), ex);
        Sentry.captureException(ex);
        return Response.fail(ExceptionEnum.INNERNAL_ERROR.getCode(), ExceptionEnum.INNERNAL_ERROR.getReason());
    }

    private static String desensitization(String log) {
        return MessageFormat.format(log, "***", "***", "***", "***", "***", "***", "***", "***");
    }

}
