package com.skio.view_model;

import android.app.Application;
import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.skio.R;
import com.skio.api.ApiService;
import com.skio.api.CallBack;
import com.skio.base.BaseModel;
import com.skio.dialog.CustomDialog;
import com.skio.entity.ClockEntity;
import com.skio.entity.EmptyEntity;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.List;

import retrofit.http.observer.HttpRxObservable;


/**
 * @Description:测试MVVM更新UI流程
 * @Author: LiuYX
 */

public class MainViewModel extends BaseModel {

    private MutableLiveData<EmptyEntity> title;
    private MutableLiveData<List<ClockEntity>> clockTime;
    private Long[] clockTimeArray=new Long[]{60L,100L,200L,400L,10L,30L,20L,90L,35L,28L,12L,39L,40L,122L,34L,66L,88L,33L,22L,10L,56L,32L,54L,23L,45L,66L,33L,95L,32L,231L,434L,231L};
    private List<ClockEntity> clockEntities=new ArrayList<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<EmptyEntity> getTitle() {
        if (title == null) {
            title = new MutableLiveData();
            EmptyEntity emptyEntity= new EmptyEntity();
            emptyEntity.setCityName("测试");
            title.setValue(emptyEntity);
            //TODO可以从网络获取
            //.......
        }
        return title;
    }


    //获取线路数据
    public void getLines(String json){
        HttpRxObservable
                .getObservable(ApiService.object.getInstance()
                        .getUserService().getUserCity(translateJson(json)), (LifecycleProvider) getActivity())
                .subscribe(new CallBack<List<EmptyEntity>>(null) {
                    @Override
                    protected void onSuccess(List<EmptyEntity> response) {
                        super.onSuccess(response);
                        if(response!=null&&response.size()>0){
                            title.setValue(response.get(0));
                        }
                    }
                });
    }




    public MutableLiveData<List<ClockEntity>> getClockTime() {
        if (clockTime == null) {
            clockTime = new MutableLiveData();
            for (int i=0;i<clockTimeArray.length;i++){
                ClockEntity clockEntity=new ClockEntity();
                clockEntity.setName("闹钟"+i);
                clockEntity.setTime(clockTimeArray[i]*1000);
                clockEntities.add(clockEntity);
                clockTime.setValue(clockEntities);
            }
        }
        return clockTime;
    }

    public void getDialog(){
        CustomDialog commomDialog = new CustomDialog(getActivity(), R.style.dialog, "我是谁，我在哪？ --  VollegeTargetActivity", new CustomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                dialog.dismiss();
            }
        });
        commomDialog.setTitle("提示").show();

    }


}
