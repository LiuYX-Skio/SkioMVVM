package retrofit.http.intercept;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit.utils.AppUtils;

/**
 * 发送请求拦截器
 * Created by aojiaoqiang on 2018/1/31.
 */

public class RequestIntercept implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 设置一些公用的请求头
        Request request = chain.request();
        request.newBuilder()
                // 插入语言版本
                .header("Accept-Language", AppUtils.getLocal())
                .build();
        return chain.proceed(request);
    }

}
