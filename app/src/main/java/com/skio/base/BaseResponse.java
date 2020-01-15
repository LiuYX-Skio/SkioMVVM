package com.skio.base;

import com.google.gson.annotations.SerializedName;

/**
 * 作者：LiuYX on 2017/12/28 11:19
 *
 * 返回的数据结构：
 * {
 "data": [],
 "errorCode": 0,
 "errorMsg": ""
 }
 */

public class BaseResponse<T> {

    //序列化名称  拿到我们想要的msg字段而不是errorMsg字段
    @SerializedName("msg")
    private String msg;
    @SerializedName("code")
    private int code;

    private T result;


    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return result;
    }


}
