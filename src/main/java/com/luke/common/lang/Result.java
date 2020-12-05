package com.luke.common.lang;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 结果封装类
 * @Author luke
 * @Date 2020/11/29 23:24
 */
@Data
public class Result implements Serializable {

    /**
     * 状态标识
     * 200-正常
     * 非200-异常
     */
    private int code;
    private String msg;
    private Object data;

    public static Result succ(Object data) {
        return succ(200, "操作成功", data);
    }

    /**
     * 成功返回消息
     * @param code 状态标识
     * @param msg 信息
     * @param data 数据
     * @return 结果类
     */
    public static Result succ(int code, String msg, Object data) {
        Result result = new Result();
        result.code = code;
        result.msg = msg;
        result.data = data;
        return result;
    }

    public static Result fail(String msg) {
        return fail(400, msg, null);
    }

    public static Result fail(String msg, Object data) {
        return fail(400, msg, data);
    }

    public static Result fail(int code, String msg, Object data) {
        Result result = new Result();
        result.code = code;
        result.msg = msg;
        result.data = data;
        return result;
    }


}
