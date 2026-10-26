package com.jiancai.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一接口返回对象
 */
@Data
public class R<T> implements Serializable {

    /** 200=成功，其它=失败 */
    private Integer code;
    private String msg;
    private T data;

    public static <T> R<T> ok() {
        return build(200, "操作成功", null);
    }

    public static <T> R<T> ok(T data) {
        return build(200, "操作成功", data);
    }

    public static <T> R<T> ok(String msg, T data) {
        return build(200, msg, data);
    }

    public static <T> R<T> fail(String msg) {
        return build(500, msg, null);
    }

    public static <T> R<T> fail(Integer code, String msg) {
        return build(code, msg, null);
    }

    private static <T> R<T> build(Integer code, String msg, T data) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
}
