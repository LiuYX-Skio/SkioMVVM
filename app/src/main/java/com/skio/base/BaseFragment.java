package com.skio.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.skio.entity.EmptyBean;
import com.skio.util.DynamicTimeFormat;
import com.trello.rxlifecycle2.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 作者：LiuYX on 2017/12/27 15:59
 * 父类->基类->动态指定类型->泛型设计（通过泛型指定动态类型->由子类指定，父类只需要规定范围即可）
 */
public abstract class BaseFragment<V extends BaseModel,B extends ViewDataBinding> extends RxFragment {

    //引用V层和P层
    public Context mContext;
    protected View mRootView;
    public InputMethodManager inputMethodManager;
    public RefreshLayout refreshLayout;
    //分页起始页
    public int position=1;
    public <T extends View> T findView(View v,int id) {
        return (T) v.findViewById(id);
    }

    private V mView;
    private B viewDataBinding;


    public V getMView(){
        return mView;
    }
    public B getDataBinding(){
        return viewDataBinding;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mRootView == null) {
            install();
            mRootView = inflater.inflate(getLayoutId(),container,false);
        }
        mContext = getActivity();
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        EventBus.getDefault().register(this);
        if(mView==null){
            mView = createView();
            mView.initData(getActivity());
        }
        if(viewDataBinding==null){
            viewDataBinding = createViewDataBinding();
        }
        setViewData();
        initListener();
        init();

        initBaseView(false);
        ViewGroup group=(ViewGroup) mRootView.getParent();
        if(group!=null){
            group.removeView(mRootView);
        }
        return mRootView;
    }

    //由子类指定具体类型
    public abstract int getLayoutId();
    public abstract void init();

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
    //事件初始化
    public abstract void initListener();
    //创建viewModel
    public abstract V createView();
    //创建view
    public abstract B createViewDataBinding();
    //绑定数据
    public abstract void setViewData();

    public void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /*
     * 关键代码，需要在布局生成之前设置，建议代码放在 Application 中
     */
    private static void install() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @SuppressLint("ResourceType")
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                ClassicsHeader header = new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.FixedBehind);
                header.setPrimaryColorId(Color.parseColor("#8D9296"));
                header.setAccentColorId(android.R.color.white);
                return header;//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setEnableLoadMoreWhenContentNotFull(true);//内容不满一页时候启用加载更多
                ClassicsFooter footer = new ClassicsFooter(context);
                footer.setBackgroundResource(android.R.color.white);
                footer.setSpinnerStyle(SpinnerStyle.Scale);//设置为拉伸模式
                return footer;//指定为经典Footer，默认是 BallPulseFooter
            }
        });
    }
    //还原默认 Header
    private static void restore() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @SuppressLint("ResourceType")
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setPrimaryColorsId(Color.parseColor("#8D9296"), android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context).setTimeFormat(new DynamicTimeFormat("更新于 %s"));
            }
        });
    }

    //调用刷新，此方法必须调用
    public void initBaseView(boolean isLode){
        if(!isLode){
            return;
        }
//        refreshLayout=findView(mRootView, R.id.refreshLayout);
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
                position=1;
                getLodeData();
            }
        });

    };

    public void getLodeData(){

    }

    public void getLodeSuccess(){
        if(position==1){
            refreshLayout.finishRefresh();
        }else {
            refreshLayout.finishLoadMore();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendEventBus(EmptyBean emptyBean){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        restore();
        EventBus.getDefault().unregister(this);
        //防止快速点击的时候销毁会为空
        if(mRootView==null){
            return;
        }
        ViewGroup group=(ViewGroup) mRootView.getParent();
        if(group!=null){
            group.removeView(mRootView);
        }
    }



}