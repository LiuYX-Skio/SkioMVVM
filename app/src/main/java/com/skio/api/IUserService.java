package com.skio.api;

import com.skio.entity.EmptyEntity;
import com.skio.entity.HttpResponse;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IUserService {

    @POST(ApiConfig.UserApi.USERCITY)
    Observable<HttpResponse<List<EmptyEntity>>> getUserCity(@Body RequestBody requestBody);




}