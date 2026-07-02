package com.icbc.sh.techmg.common.model;

import com.icbc.sh.techmg.common.constant.ResultCode;
import lombok.Data;

@Data
public class R<T> {

    private int code;
    private String message;
    private T data;

    private R() {}

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.message = ResultCode.SUCCESS.getMessage();
        r.data = data;
        return r;
    }

    public static <T> R<T> fail(ResultCode code) {
        return fail(code, code.getMessage());
    }

    public static <T> R<T> fail(ResultCode code, String message) {
        R<T> r = new R<>();
        r.code = code.getCode();
        r.message = message;
        return r;
    }

    public static <T> R<T> fail(int code, String message) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        return r;
    }

    public static <T> R<T> fail(int code, String message, T data) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        r.data = data;
        return r;
    }
}
