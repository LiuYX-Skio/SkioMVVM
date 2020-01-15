package retrofit.http.gson;

import android.content.Context;

import okhttp3.Interceptor;

/**
 * @class describe
 * @anthor aojiaoqiang
 * @time 2018/8/31 16:25
 */
public interface IGsonConverterFactory {
    /**
     * 获取Gson拦截器
     *
     * @return
     */
    CustomGsonConverterFactory create();

    /**
     * 添加拦截器
     * @return
     */
    Interceptor getInterceptor();

    Context getContext();

    String getBaseUrl();
}
