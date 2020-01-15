package retrofit.http.function;




import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import retrofit.http.exception.ExceptionEngine;

/**
 * http结果处理函数
 *
 */
public class HttpResultFunction<T> implements Function<Throwable, Observable<T>> {

    @Override
    public Observable<T> apply(@NonNull Throwable throwable) throws Exception {
        //打印具体错误
        Log.d("HttpResultFunction:" ,"==="+ throwable);
        return Observable.error(ExceptionEngine.handleException(throwable));
    }

}
