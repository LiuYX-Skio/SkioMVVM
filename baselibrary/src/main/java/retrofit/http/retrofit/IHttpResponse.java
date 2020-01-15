package retrofit.http.retrofit;

/**
 * @class describe  判断网络
 * @anthor aojiaoqiang
 * @time 2018/8/31 15:33
 */
public interface IHttpResponse<T> {

    int getCode();
    String getMsg();
    T getResult();
    void setResult(T result);
    /**
     * API是否请求失败
     *
     * @return 失败返回true, 成功返回false
     */
    boolean isCodeInvalid();
}
