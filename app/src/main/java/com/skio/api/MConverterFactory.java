package com.skio.api;

import android.content.Context;

import com.google.gson.Gson;

import okhttp3.Interceptor;
import retrofit.http.gson.CustomGsonConverterFactory;
import retrofit.http.gson.IGsonConverterFactory;

/**
 * @class describe
 * @anthor aojiaoqiang
 * @time 2018/10/19 18:57
 */
public class MConverterFactory implements IGsonConverterFactory {
    private Context mContext;
    private Gson gson;
    private MGsonConverter converter;
    private CustomGsonConverterFactory factory;
    private MHeaderInterceptor interceptor;

    public MConverterFactory(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public CustomGsonConverterFactory create() {
        if (gson == null)
            gson = new Gson();
        if (converter == null)
            converter = new MGsonConverter();
        if (factory == null)
            factory = new CustomGsonConverterFactory(gson, converter);
        return factory;
    }

    @Override
    public Interceptor getInterceptor() {
        if (interceptor == null)
            interceptor = new MHeaderInterceptor();
        return interceptor;
    }


    @Override
    public Context getContext() {
        return mContext;
    }


    @Override
    public String getBaseUrl() {
        return ApiConfig.getBaseUrl();
    }


}
