package com.skio.api;

/**
 * Created by wuwen on 2018/9/15
 */
public class ApiConfig {

    // 后端接口处理成功
    public static final int SUCCESS = 200;
    // token已过期
    public static final int TOKEN_TIME_OUT = 450;
    private static String DEBUG_BASE_LOCAL_URL = "http://116.236.99.82:10203/";


    public static String getBaseUrl() {
        return DEBUG_BASE_LOCAL_URL;
    }


    public static String setBaseUrl(String url) {
        DEBUG_BASE_LOCAL_URL=url;
        return DEBUG_BASE_LOCAL_URL;
    }




    public interface UserApi {
        String USERCITY = "app/shipper/order/zeroLoad/listOrderZeroLoadFromCity";

    }




}
