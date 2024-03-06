package com.jintao.secret;

public class EncrypyUtils {

    static {
        System.loadLibrary("encrypt");
    }

    public native String encode(String str);
    public native String decode(String str);
}
