package com.trading.backend.http;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ~~ trading.s
 * @date 16:04 09/22/21
 */
@Getter
@Setter
public class Response<T> {

    private int code;

    private String msg;

    private T data;

    public static <T> Response<T> ok() {
        Response<T> response = new Response<>();
        response.setMsg("ok");
        return response;
    }

    public static <T> Response<T> ok(String msg) {
        Response<T> response = new Response<>();
        response.setMsg(msg);
        return response;
    }

    public static <T> Response<T> ok(T data) {
        Response<T> response = ok();
        response.setData(data);
        return response;
    }

    public static <T> Response<T> ok(T data, String msg) {
        Response<T> response = ok();
        response.setData(data);
        response.setMsg(msg);
        return response;
    }

    public static <T> Response<T> fail() {
        Response<T> response = new Response<>();
        response.setCode(-1);
        return response;
    }

    public static <T> Response<T> fail(String msg) {
        Response<T> response = fail();
        response.setMsg(msg);
        return response;
    }

    public static <T> Response<T> fail(int code, String msg) {
        Response<T> response = fail();
        response.setCode(code);
        response.setMsg(msg);
        return response;
    }
}
