package com.skio.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import retrofit.AppManager;
import retrofit.base.listener.LifeCycleListener;


/**
 * 作者：LiuYX on 2017/12/27 10:09
 * 父类->基类->动态指定类型->泛型设计（通过泛型指定动态类型->由子类指定，父类只需要规定范围即可）
 */
public abstract class BaseActivity<V extends BaseModel,B extends ViewDataBinding> extends RxAppCompatActivity {
    private V view;
    private B viewDataBinding;

    public <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    public V getView(){
        return view;
    }
    public B getDataBinding(){
        return viewDataBinding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(view==null){
            view = createView();
            view.initData(this);
        }
        if(viewDataBinding==null){
            viewDataBinding = createViewDataBinding();
        }
        setViewData();
        initListener();
        init();
    }


    //事件初始化
    public abstract void initListener();
    //创建viewModel
    public abstract V createView();
    //创建view
    public abstract B createViewDataBinding();
    //初始化信息
    public abstract void init();
    //绑定数据
    public abstract void setViewData();

    public RefreshLayout refreshLayout;
    public int position= ConstantCode.PAGE_CODE;

    public void initBaseView(boolean isLode){
        if(!isLode){
            return;
        }
//        refreshLayout=findView(R.id.refreshLayout);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                refreshLayout.finishLoadMore();
                position++;
                getLodeData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                refreshLayout.finishRefresh();
                position=ConstantCode.PAGE_CODE;
                getLodeData();

            }
        });

    };

    public void getLodeData(){

    }

    public void getLodeSuccess(){
        if(position==ConstantCode.PAGE_CODE){
            refreshLayout.finishRefresh();
        }else {
            refreshLayout.finishLoadMore();
        }
    }

    @SuppressLint("RestrictedApi")
    public void replaceFragment(FragmentManager manager, Fragment to, int resId) {
        FragmentTransaction transaction = manager.beginTransaction();
        //遍历隐藏所有添加的fragment
        for (Fragment fragment : manager.getFragments()) {
            transaction.hide(fragment);
        }
        if (!to.isAdded()) { //若没有添加过
            transaction.add(resId, to).commit();
        } else { //若已经添加
            transaction.show(to).commit();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mListener != null) {
            mListener.onDestroy();
        }
        AppManager.getAppManager().finishActivity(this);
    }

    /**
     * 生命周期回调函数
     */
    public LifeCycleListener mListener;

    public void setOnLifeCycleListener(LifeCycleListener listener) {
        mListener = listener;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mListener != null) {
            mListener.onStart();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mListener != null) {
            mListener.onRestart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mListener != null) {
            mListener.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mListener != null) {
            mListener.onStop();
        }
    }

}
