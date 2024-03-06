package com.jintao.vipmanager.network;

public class HttpData<T> {

    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
//        isTokenFailure();
        return message;
    }

    public T getData() {
        return data;
    }
    
    public boolean isRequestSucceed() {
        return code == 200;
    }

    public boolean isTokenFailure() {
        return false;
    }
}