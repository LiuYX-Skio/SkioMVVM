package retrofit.http.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by 文强 on 2017/3/2.
 */

public class CustomGsonConverterFactory<I extends IGsonConverter> extends Converter.Factory {

    private final Gson gson;
    private final I mGsonConverter;

    public CustomGsonConverterFactory(Gson gson, I gsonConverter) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
        mGsonConverter = gsonConverter;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        try {
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
            return new CustomGsonResponseBodyConverter<>(gson, mGsonConverter, adapter);
        } catch (Exception e) {
            e.printStackTrace();
            return new CustomGsonResponseBodyConverter<>(null, mGsonConverter, null);
        }
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        try {
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
            return new CustomGsonRequestBodyConverter<>(gson, adapter);
        } catch (Exception e) {
            e.printStackTrace();
            return new CustomGsonRequestBodyConverter<>(null, null);
        }
    }
}