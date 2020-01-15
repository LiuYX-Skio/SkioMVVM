package retrofit.http.gson;


import retrofit.http.retrofit.IHttpResponse;

/**
 * @class describe 这里处理Gson拦截器的json拦截，json 预处理 比如code msg的判断，是否成功失败，登录过期等等
 * @anthor aojiaoqiang
 * @time 2018/8/31 16:10
 */
public interface IGsonConverter {
    /**
     * @param json gson拦截器的json预处理
     * @return  返回true：json预处理通过，比如code = 成功；false json处理失败，比如登录过期，服务器返回的错误
     */
    boolean gsonConvert(String json);
    IHttpResponse getHttpResponse();
}
