package com.jintao.vipmanager.network;

import com.hjq.http.config.IRequestServer;

import com.jintao.vipmanager.utils.AppConfig;

public class HttpServerUrl implements IRequestServer {

    @Override
    public String getHost() {
        return AppConfig.BASE_RELEASE_URL;
    }
}