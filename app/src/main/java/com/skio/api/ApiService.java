package com.skio.api;

import retrofit.http.retrofit.RetrofitUtils;

public class ApiService {

    public IUserService getUserService(){
        return create(IUserService.class);
    }

    public   <T> T create(Class<T> cls) {
        return RetrofitUtils.getInstance().create(cls);
    }

    public static class object {
        public static volatile ApiService instance = null;
        public static ApiService getInstance() {
        if (instance == null) {
            synchronized(ApiService.class) {
                if (instance == null) {
                    instance =new ApiService();
                }
            }
        }
        return instance;
        }
    }

}
