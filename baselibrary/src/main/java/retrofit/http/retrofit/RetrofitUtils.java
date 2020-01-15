package retrofit.http.retrofit;

import android.annotation.SuppressLint;
import android.content.Context;


import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.lyx.skio.BuildConfig;

import java.util.concurrent.TimeUnit;

import me.jessyan.progressmanager.ProgressManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit.http.gson.IGsonConverterFactory;
import retrofit.http.intercept.ResponseInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * RetrofitUtils工具类
 */
@SuppressLint("StaticFieldLeak")
public final class RetrofitUtils {

    private static final int CONNECT_TIME_OUT = 30;//连接超时时长x秒
    private static final int READ_TIME_OUT = 30;//读数据超时时长x秒
    private static final int WRITE_TIME_OUT = 30;//写数据接超时时长x秒

    private Retrofit mRetrofit;
    private OkHttpClient okHttpClient;
    private static volatile RetrofitUtils instance;
    private static IGsonConverterFactory mGsonfactory;
    private IGsonConverterFactory mFactory;

    public static void init(IGsonConverterFactory factory) {
        mGsonfactory = factory;
    }

    public static synchronized RetrofitUtils getInstance() {
        if (instance == null) {
            synchronized (RetrofitUtils.class) {
                if (instance == null) {
                    instance = new RetrofitUtils(mGsonfactory);
                }
            }
        }
        return instance;
    }

    private RetrofitUtils() {
    }

    private RetrofitUtils(IGsonConverterFactory factory) {
        mFactory = factory;
        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient(mFactory.getContext()))
                .baseUrl(mFactory.getBaseUrl())
                .addConverterFactory(mFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 设置okHttp
     */
    private OkHttpClient okHttpClient(Context context) {
        if (okHttpClient == null) {
            // 构建 OkHttpClient 时,将 OkHttpClient.Builder() 传入 with() 方法,进行初始化配置 用于监听上传下载 url作为key
            okHttpClient = ProgressManager.getInstance().with(builderHttp(context)).build();
        }
        return okHttpClient;
    }

    private OkHttpClient.Builder builderHttp(Context context) {
        //开启Log
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                // 失败是否重新请求
                .retryOnConnectionFailure(true);
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(logging);
        }
        builder.addInterceptor(new ResponseInterceptor());
        // 保存session
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        builder.cookieJar(cookieJar);
        // 添加自定义的拦截器
        if (mFactory != null && mFactory.getInterceptor() != null) {
            builder.addInterceptor(mFactory.getInterceptor());
        }
        return builder;
    }

    /**
     * 获取Retrofit
     */
    private Retrofit getRetrofit() {
        return mRetrofit;
    }

    public <T> T create(Class<T> cls) {
        return getRetrofit().create(cls);
    }

}
