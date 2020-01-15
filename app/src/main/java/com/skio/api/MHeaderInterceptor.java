package com.skio.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @class describe
 * @anthor aojiaoqiang
 * @time 2018/10/19 18:19
 */
public class MHeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request updateRequest = originalRequest.newBuilder()
                    .addHeader("appVersion","1.0.1")
                .addHeader("token","67c64d1359d940dc9d329040229e0a70a2f765be8fc67f500b334462a306d5d9")
                    .addHeader("Content-Type","application/json; charset=utf-8").build();
            return chain.proceed(updateRequest);
    }
}
