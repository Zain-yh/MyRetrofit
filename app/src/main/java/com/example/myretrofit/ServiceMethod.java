package com.example.myretrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class ServiceMethod {

    private final HttpUrl baseUrl;
    private final String relativeUrl;
    private final String httpMethod;
    private final Call.Factory callFactory;
    private final ParameterHandler[] parameterHandlers;
    private FormBody.Builder formBuilder;
    private HttpUrl.Builder urlBuilder;

    public ServiceMethod(Builder builder) {
        baseUrl = builder.myRetrofit.baseUrl;
        relativeUrl = builder.relativeUrl;
        httpMethod = builder.httpMethod;
        callFactory = builder.myRetrofit.callFactory;
        parameterHandlers = builder.parameterHandlers;
        //判断是否需要请求体
        if (builder.hasBody) {
            formBuilder = new FormBody.Builder();
        }
    }


    public Object invoke(Object[] args){
        //由于有多个参数，先要把url拼凑起来
        for (int i = 0; i < parameterHandlers.length; i++) {
            ParameterHandler parameterHandler = parameterHandlers[i];
            //由于arg中本就记录了所有参数的值，只需要当作value传进去就行
            parameterHandler.apply(this, args[i].toString());
        }
        HttpUrl url;
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
        }
        url = urlBuilder.build();

        //请求体
        FormBody formBody = null;
        if (formBuilder != null) {
            formBody = formBuilder.build();
        }
        //发起请求
        Request request = new Request.Builder().url(url).method(httpMethod, formBody).build();
        return callFactory.newCall(request);
    }

    //将key value 的数据放入请求体中
    public void addFieldParam(String key, String value){
        formBuilder.add(key, value);
    }
    //或拼接到url之后
    public void addQueryParam(String key, String value){
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
        }
        urlBuilder.addQueryParameter(key, value);
    }

    public static class Builder {

        public MyRetrofit myRetrofit;
        private final Annotation[] methodAnnotations;
        private final Annotation[][] parameterAnnotations;
        public String httpMethod;
        public String relativeUrl;
        public boolean hasBody = false;
        public ParameterHandler[] parameterHandlers;

        public Builder(MyRetrofit myRetrofit, Method method){
            this.myRetrofit = myRetrofit;
            //获取所有方法上的注解
            methodAnnotations = method.getAnnotations();
            //获取所有参数的注解， 可能有多个参数，且每个参数注解可能不止一个，所以是二维数组
            parameterAnnotations = method.getParameterAnnotations();
        }


        public ServiceMethod build(){
            //1.先解析方法上的注解，此处只处理 Post 和 Get
            for (Annotation methodAnnotation : methodAnnotations) {
                if (methodAnnotation instanceof POST){
                    //记录当前请求的方式
                    this.httpMethod = "POST";
                    //获取并记录对应注解的数据
                    this.relativeUrl = ((POST) methodAnnotation).value();
                    //POST请求需要请求体
                    hasBody = true;
                }
                if (methodAnnotation instanceof GET) {
                    this.httpMethod = "GET";
                    this.relativeUrl = ((GET) methodAnnotation).value();
                    hasBody = false;
                }
            }

            //2.解析每个参数上的注解和注解对应的value
            int length = parameterAnnotations.length;
            parameterHandlers = new ParameterHandler[length];
            for (int i = 0; i < length; i++) {
                //获取当前参数上的所有注解
                Annotation[] annotations = parameterAnnotations[i];
                //处理参数上的每一个注解
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Field){
                        String value = ((Field) annotation).value();
                        parameterHandlers[i] = new ParameterHandler.FieldParamHandler(value);
                    }
                    if (annotation instanceof Query){
                        String value = ((Query) annotation).value();
                        parameterHandlers[i] = new ParameterHandler.QueryParamHandler(value);
                    }
                }
            }
            return new ServiceMethod(this);
        }
    }
}
