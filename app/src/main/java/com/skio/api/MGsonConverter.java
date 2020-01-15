package com.skio.api;

import com.google.gson.Gson;
import com.skio.entity.HttpStatus;

import retrofit.http.gson.IGsonConverter;
import retrofit.http.retrofit.IHttpResponse;

/**
 * Created by wuwen on 2018/9/15
 */
public class MGsonConverter implements IGsonConverter {

    private IHttpResponse httpResponse;


    @Override
    public boolean gsonConvert(String json) {
        httpResponse = new Gson().fromJson(json, HttpStatus.class);
        return httpResponse != null && httpResponse.isCodeInvalid();
    }


    @Override
    public IHttpResponse getHttpResponse() {
        return httpResponse;
    }


}
