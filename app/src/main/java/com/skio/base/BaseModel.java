package com.skio.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit.base.listener.LifeCycleListener;

public class BaseModel<T extends Activity> extends AndroidViewModel implements LifeCycleListener {

    protected Reference<T> mActivityRef;
    protected T mActivity;

    public BaseModel(@NonNull Application application) {
        super(application);

    }
    public void initData(T activity){
        attachActivity(activity);
        setListener(activity);
    }


    /**
     * 设置生命周期监听
     *
     * @author ZhongDaFeng
     */
    private void setListener(T activity) {
        if (getActivity() != null) {
            if (activity instanceof BaseActivity) {
                ((BaseActivity) getActivity()).setOnLifeCycleListener(this);
            }
        }
    }

    /**
     * 获取
     *
     * @return
     */
    public T getActivity() {
        if (mActivityRef == null) {
            return null;
        }
        return mActivityRef.get();
    }

    /**
     * 关联
     *
     * @param activity
     */
    private void attachActivity(T activity) {
        mActivityRef = new WeakReference<T>(activity);
        mActivity = mActivityRef.get();
    }


    /**
     * 销毁
     */
    private void detachActivity() {
        if (isActivityAttached()) {
            mActivityRef.clear();
            mActivityRef = null;
        }
    }
    /**
     * 是否已经关联
     *
     * @return
     */
    public boolean isActivityAttached() {
        return mActivityRef != null && mActivityRef.get() != null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        detachActivity();
    }

    /**
     * @功能 将json格式转化为RequestBody格式，方便进行请求接口
     */
    public RequestBody translateJson(String json) {
        return RequestBody.create(MediaType.parse("application/json"), json);
    }

}
