# SkioMVVM

#### 非常提高开发效率的MVVM架构模式

#### 软件架构

1.MVVM+LiveData+Retrofit2+okhttp框架搭建


#### 根据手机分辨率自动计算宽高值，完成全手机百分百适配


1.支持只按宽度适配

2.宽度高度同时适配

3.字体适配

4.拓展性极强，特殊手机拓展性极强，举一个极端的例子例如正方形手机不能按照正常分辨率去进行宽高计算，这个时候需要特殊处理，你可以手动进行判断，1:1的手机按特殊比例处理宽高

5.每一步都有代码示例

#### 刷新控件

1.非常好看的下拉刷新上拉加载(SmartRefreshLayout)

2.新增支持下拉刷新之后下拉到底部禁止滑动，上拉加载之后滑动到顶部禁止下拉


#### 项目详细介绍MVVM架构模式

在build打开打开databinding
![打开databinding](https://images.gitee.com/uploads/images/2020/0111/161443_6c426ff6_684888.png "屏幕截图.png")

在基类封装一层databinding和ViewModel

![在基类封装一层databinding和ViewModel](https://images.gitee.com/uploads/images/2020/0111/161715_f558e4bd_684888.png "屏幕截图.png")

![输入图片说明](https://images.gitee.com/uploads/images/2020/0111/161837_286e4a87_684888.png "屏幕截图.png")

![输入图片说明](https://images.gitee.com/uploads/images/2020/0113/154225_ebc02b28_684888.png "屏幕截图.png")

当前设计图尺寸配置，如果width_ratio设置为true，那么布局宽高将会只会以屏幕宽度计算比例，如果为false布局宽度则会以屏幕宽度单独计算比例，布局高度则会单独以屏幕高度计算比例

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"
    xmlns:tools="[http://schemas.android.com/tools](http://schemas.android.com/tools)"
    package="com.skio">
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:name=".AppManager"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:usesCleartextTraffic="true"
        android:supportsRtl="true"
        tools:replace="android:icon"
        android:theme="@style/AppTheme">
        <!--设计图尺寸宽度-->
        <meta-data android:name="design_width" android:value="720"/>
        <!--设计图尺寸高度,当设置只进行宽度适配时此属性无效-->
        <meta-data android:name="design_height" android:value="1280"/>
        <!--是否只按宽度适配，默认按照宽高同时适配-->
        <meta-data android:name="width_ratio" android:value="true"/>
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>

这部分为ViewModel映射到UI上的示例代码

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


}
 
RecyclerView数据映射代码示例

![输入图片说明](https://images.gitee.com/uploads/images/2020/0113/153503_c6e399c9_684888.png "屏幕截图.png")


#### 参与贡献

刘宇轩(LiuYX)



微信公众号：我的Android架构师

