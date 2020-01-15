package com.skio.api;


import com.skio.util.OnTokenTimeOutEvent;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.disposables.Disposable;
import retrofit.base.mvp.IBaseView;
import retrofit.http.exception.ApiException;
import retrofit.http.observer.HttpRxObserver;

/**
 * Created by wuwen on 2018/10/13
 */
public abstract class CallBack<T> extends HttpRxObserver<T> {
    private IBaseView mView;

    public CallBack(IBaseView mView) {
        this.mView = mView;
    }

    @Override
    protected void onStart(Disposable d) {
        if (mView != null) mView.showLoading();
    }

    @Override
    protected void onError(ApiException e) {
        if (e.getCode() == ApiConfig.TOKEN_TIME_OUT) {
            // 登录过期
            EventBus.getDefault().post(new OnTokenTimeOutEvent());
        }
        onFailure(e);
    }

    @Override
    protected void onSuccess(T response) {
        if (mView != null) {
            mView.closeLoading();
        }
    }

    protected void onFailure(ApiException e) {
        if (mView != null) {
            mView.closeLoading();
            mView.showToast(e.getMsg());
        }
    }

}
