package com.example.myretrofit;

public abstract class ParameterHandler {

    //类似于回调
    abstract void apply(ServiceMethod serviceMethod, String value);

    public static class FieldParamHandler extends ParameterHandler{

        private String key;

        public FieldParamHandler(String key){
            this.key = key;
        }

        @Override
        void apply(ServiceMethod serviceMethod, String value) {
            serviceMethod.addFieldParam(key, value);
        }
    }

    public static class QueryParamHandler extends ParameterHandler {

        private String key;

        public QueryParamHandler(String key){
            this.key = key;
        }

        @Override
        void apply(ServiceMethod serviceMethod, String value) {
            serviceMethod.addQueryParam(key, value);
        }
    }
}
