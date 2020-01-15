package com.skio;


import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSONObject;
import com.skio.adapter.AlarmClockAdapter;
import com.skio.base.BaseActivity;
import com.skio.databinding.ActivityMainBinding;
import com.skio.view_model.MainViewModel;


/**
 * 作者：LiuYX on 2020/01/11 10:09
 * 首页
 */

public class MainActivity extends BaseActivity<MainViewModel, ActivityMainBinding> implements View.OnClickListener {
    private AlarmClockAdapter alarmClockAdapter;



    @Override
    public MainViewModel createView() {
        return ViewModelProviders.of(this).get(MainViewModel.class);
    }


    @Override
    public ActivityMainBinding createViewDataBinding() {
        return DataBindingUtil.setContentView(this,R.layout.activity_main);
    }


    @Override
    public void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        getDataBinding().recyclerView.setLayoutManager(layoutManager);
        alarmClockAdapter = new AlarmClockAdapter(this);
        getDataBinding().recyclerView.setAdapter(alarmClockAdapter);
        alarmClockAdapter.refreshData(getView().getClockTime().getValue());
    }

    @Override
    public void initListener() {
        getDataBinding().btDataBinding.setOnClickListener(this);
    }


    @Override
    public void setViewData() {
        getDataBinding().setData(getView());
        getDataBinding().setLifecycleOwner(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_dataBinding:
                getView().getDialog();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("type","1");
                getView().getLines(jsonObject.toJSONString());
                break;
        }
    }
}
