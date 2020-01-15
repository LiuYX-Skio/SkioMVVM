package retrofit.http.exception;

import android.util.Log;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.ParseException;

import retrofit2.HttpException;


/**
 * 错误/异常处理工具
 *
 */
public class ExceptionEngine {

    public static final int UN_KNOWN_ERROR = 1000;//未知错误
    public static final int ANALYTIC_SERVER_DATA_ERROR = 1001;//解析(服务器)数据错误
    public static final int ANALYTIC_CLIENT_DATA_ERROR = 1002;//解析(客户端)数据错误
    public static final int CONNECT_ERROR = 1003;//网络连接错误
    public static final int TIME_OUT_ERROR = 1004;//网络连接超时

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        Log.w("http错误信息",""+e.getMessage());
        if (e instanceof HttpException) {             //HTTP错误
            HttpException httpExc = (HttpException) e;
            ex = new ApiException(e, UN_KNOWN_ERROR);
            ex.setMsg("网络错误");  //均视为网络错误
            return ex;
        } else if (e instanceof ServerException) {    //服务器返回的错误
            ServerException serverExc = (ServerException) e;
            ex = new ApiException(serverExc, serverExc.getCode());
            ex.setMsg(serverExc.getMsg());
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException || e instanceof MalformedJsonException) {  //解析数据错误
            ex = new ApiException(e, UN_KNOWN_ERROR);
            ex.setMsg("数据解析错误，请重试");
            return ex;
        } else if (e instanceof ConnectException) {//连接网络错误
            ex = new ApiException(e, UN_KNOWN_ERROR);
            ex.setMsg("连接失败，请重试");
            return ex;
        } else if (e instanceof SocketTimeoutException) {//网络超时
            ex = new ApiException(e, UN_KNOWN_ERROR);
            ex.setMsg("网络连接超时，请重试");
            return ex;
        } else {  //未知错误
            ex = new ApiException(e, UN_KNOWN_ERROR);
            ex.setMsg("网络不给力，请稍候再试");
            return ex;
        }
    }

}
