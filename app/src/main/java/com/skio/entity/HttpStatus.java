package com.skio.entity;


import com.skio.api.ApiConfig;

import retrofit.http.retrofit.IHttpResponse;

/**
 * Created by wuwen on 2018/9/15
 */
public class HttpStatus implements IHttpResponse {
    private int code;
    private String msg;

    public HttpStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public Object getResult() {
        return null;
    }

    @Override
    public void setResult(Object result) {

    }


    @Override
    public boolean isCodeInvalid() {
        return this.code != ApiConfig.SUCCESS;
    }
}
