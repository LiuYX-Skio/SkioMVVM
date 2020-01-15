package retrofit.http.gson;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit.http.exception.ExceptionEngine;
import retrofit.http.exception.ServerException;
import retrofit2.Converter;

/**
 * Created by 文强 on 2017/3/2.
 */

public class CustomGsonResponseBodyConverter<I extends IGsonConverter, T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final I mGsonConverter;

    CustomGsonResponseBodyConverter(Gson gson, I gsonConverter, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
        this.mGsonConverter = gsonConverter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        //把responsebody转为string,因为retrofit2的Response对象只能够读取一次，而我们只需要判断code和获取msg就行了
        if (gson == null || adapter == null) {
            throw new ServerException(ExceptionEngine.ANALYTIC_SERVER_DATA_ERROR, "ANALYTIC SERVER DATA ERROR");
        }
        String json = value.string();
        // 这里只是为了检测code是否==200 也就是服务器定义的访问正常,所以只解析HttpStatus中的字段,因为只要code和message就可以了
//        HttpStatus httpStatus = gson.fromJson(response, HttpStatus.class);
        if(!json.contains("result")){
            StringBuilder sb = new StringBuilder(json);//构造一个StringBuilder对象
            sb.insert(json.length()-1, ",\"result\":\"\"");//在指定的位置1，插入指定的字符串
            json = sb.toString();
        }
        Log.w("数据返回",""+json);

        if (mGsonConverter.gsonConvert(json)) {
            EventBus.getDefault().post(json);
            value.close();
            //抛出一个RuntimeException, 这里抛出的异常会到CallBack的onError()方法中统一处理
            throw new ServerException(mGsonConverter.getHttpResponse().getCode(), mGsonConverter.getHttpResponse().getMsg());
        }
        MediaType contentType = value.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(json.getBytes());
        Reader reader = new InputStreamReader(inputStream, charset);
        JsonReader jsonReader = gson.newJsonReader(reader);
        try {
            return adapter.read(jsonReader);
        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(json);
            throw new IOException();
        } finally {
            value.close();
        }
    }
}



