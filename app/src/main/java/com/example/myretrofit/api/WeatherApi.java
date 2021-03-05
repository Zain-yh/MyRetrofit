package com.example.myretrofit.api;

import okhttp3.Call;
import okhttp3.HttpUrl;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WeatherApi {

    String baseUrl = "https://restapi.amap.com/v3/weather/weatherInfo";

    @POST("/v3/weather/weatherInfo")
    Call postWeather(@Field("city") String city, @Field("key") String key);

    @GET("/v3/weather/weatherInfo")
    Call getWeather(@Query("city") String city, @Query("key") String key);
}
