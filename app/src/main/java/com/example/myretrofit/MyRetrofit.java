package com.example.myretrofit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class MyRetrofit {

    public HttpUrl baseUrl;
    public Call.Factory callFactory;
    private Map<Method, ServiceMethod> serviceMethodCache = new HashMap<>();

    public MyRetrofit(HttpUrl baseUrl, Call.Factory callFactory){
        this.baseUrl = baseUrl;
        this.callFactory = callFactory;
    }

    public <T> T create(final Class<T> service) {
        return (T)Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        //解析这个method上所有的注解信息
                        ServiceMethod serviceMethod = loadServiceMethod(method);
                        //执行
                        return serviceMethod.invoke(objects);
                    }
                });
    }

    public ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod result = serviceMethodCache.get(method);
        if (result != null) return result;
        synchronized (serviceMethodCache){
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(this, method).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

    /*
     *使用建造者模式
     */
    public static class Bulider{
        private HttpUrl baseUrl;
        private Call.Factory callFactory;

        public Bulider baseUrl(String url){
            this.baseUrl = HttpUrl.get(url);
            return this;
        }

        public Bulider callFactory(Call.Factory factory){
            this.callFactory = factory;
            return this;
        }

        public MyRetrofit build() {
            if (baseUrl == null) {
                throw  new IllegalStateException("Base url is null!");
            }
            if (callFactory == null) {
                callFactory = new OkHttpClient();
            }
            return new MyRetrofit(baseUrl, callFactory);
        }
    }
}
