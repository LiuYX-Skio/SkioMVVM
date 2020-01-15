package retrofit.http.intercept;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 结果拦截器,这个类的执行时间是返回结果返回的时候,返回一个json的String,对里面一些特殊字符做处理
 * 主要用来处理一些后台上会出现的bug,比如下面声明的这三种情况下统一替换为:null
 * Created by aojiaoqiang on 2018/1/31.
 */

public class ResponseInterceptor implements Interceptor {
    public static String url="";

    private String emptyString = ":\"\"";
    private String emptyObject = ":{}";
    private String emptyArray = ":[]";
    private String newChars = ":null";

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        url=chain.request().url().toString();
        Log.w("数据·url",""+chain.request().url());
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            String json = responseBody.string();
            MediaType contentType = responseBody.contentType();
            if (!json.contains(emptyString)) {
                ResponseBody body = ResponseBody.create(contentType, json);
                return response.newBuilder().body(body).build();
            } else {
                String replace = json.replace(emptyString, newChars);/*
                String replace1 = replace.replace(emptyObject, newChars);
                String replace2 = replace1.replace(emptyArray, newChars);*/
                String replace2 = json.replace("\"result\":null","\"result\":{}");
                ResponseBody body = ResponseBody.create(contentType, replace2);
                return response.newBuilder().body(body).build();
            }
        }
        return response;
    }
}
