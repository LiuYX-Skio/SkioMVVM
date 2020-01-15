package retrofit.http.function;


import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import retrofit.http.exception.ServerException;
import retrofit.http.retrofit.IHttpResponse;

/**
 * 服务器结果处理函数
 *
 */
public class ServerResultFunction<T> implements Function<IHttpResponse<T>, T> {

    @Override
    public T apply(@NonNull IHttpResponse<T> response) throws Exception {
        // 这里处理服务器返回的是不是错误
        // code == 1就是处理成功了，否者就是处理失败 或者登录过期
        if (response.isCodeInvalid()) {
            throw new ServerException(response.getCode(), response.getMsg());
        }
        return response.getResult();
    }
}
