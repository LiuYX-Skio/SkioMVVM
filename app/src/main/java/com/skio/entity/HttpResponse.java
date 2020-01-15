package com.skio.entity;

import com.google.gson.annotations.SerializedName;
import com.skio.api.ApiConfig;

import retrofit.http.retrofit.IHttpResponse;

/**
 * http响应参数实体类
 * 通过Gson解析属性名称需要与服务器返回字段对应,或者使用注解@SerializedName
 * 备注:这里与服务器约定返回格式
 */
public class HttpResponse<T> implements IHttpResponse<T> {

    /**
     * 描述信息
     */
    @SerializedName("msg")
    private String msg;

    /**
     * 状态码
     */
    @SerializedName("code")
    private int code;

    /**
     * 数据对象[成功返回对象,失败返回错误说明]
     */
    @SerializedName("result")
    private T result;

    @Override
    public String toString() {
        return "HttpResponse{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", result=" + result +
                '}';
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }


    public void setCode(int code) {
        this.code = code;
    }

    public T getResult() {
        //防止后端返回没有result这个字段，无法进行回调
        return result;
    }

    @Override
    public void setResult(T result) {
        this.result=result;
    }


    @Override
    public boolean isCodeInvalid() {
        return getCode() != ApiConfig.SUCCESS;
    }


}
