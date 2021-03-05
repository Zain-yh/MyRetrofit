package com.example.myretrofit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myretrofit.api.WeatherApi;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private WeatherApi weatherApi;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyRetrofit myRetrofit = new MyRetrofit.Bulider().baseUrl("https://restapi.amap.com").build();
        weatherApi = myRetrofit.create(WeatherApi.class);

    }

    public void POST(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                weatherApi.postWeather("110101", "ae6c53e2186f33bbf240a12d80672d1b")
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d(TAG, "onFailure: ");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Log.d(TAG, "POST ------onResponse: "+ response.body().string());
                            }
                        });
            }
        }).start();
    }

    public void GET(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                weatherApi.getWeather("110101", "ae6c53e2186f33bbf240a12d80672d1b")
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d(TAG, "onFailure: ");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Log.d(TAG, "GET -----onResponse: "+ response.body().string());
                            }
                        });
            }
        }).start();

    }
}