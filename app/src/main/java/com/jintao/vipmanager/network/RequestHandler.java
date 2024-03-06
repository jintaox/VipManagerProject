package com.jintao.vipmanager.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.google.gson.JsonSyntaxException;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.EasyLog;
import com.hjq.http.config.IRequestHandler;
import com.hjq.http.exception.DataException;
import com.hjq.http.exception.HttpException;
import com.hjq.http.exception.ResponseException;
import com.hjq.http.request.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import com.jintao.vipmanager.utils.MmkvUtil;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class RequestHandler implements IRequestHandler {

    @NonNull
    @Override
    public Object requestSuccess(@NonNull HttpRequest<?> httpRequest, @NonNull Response response, @NonNull Type type) throws Exception {
        if (Response.class.equals(type)) {
            return response;
        }

        if (!response.isSuccessful()) {
            // 返回响应异常
            throw new ResponseException("responseCode: " +
                    response.code() + ", message: " + response.message(), response);
        }

        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }

        if (ResponseBody.class.equals(type)) {
            return body;
        }
        if (InputStream.class.equals(type)) {
            return body.byteStream();
        }
        if (Bitmap.class.equals(type)) {
            return BitmapFactory.decodeStream(body.byteStream());
        }

        String text = "";
        try {
            text = body.string();
        } catch (IOException e) {
            // 返回结果读取异常
            throw new DataException("", e);
        }
        EasyLog.printJson(httpRequest, text);
        Object result;

        try {
            result = GsonFactory.getSingletonGson().fromJson(text, type);
        } catch (JsonSyntaxException e) {
            // 返回结果读取异常
            throw new DataException("", e);
        }
        return result;
    }

    @NonNull
    @Override
    public Exception downloadFail(@NonNull HttpRequest<?> httpRequest, @NonNull Exception e) {
        return requestFail(httpRequest, e);
    }
    @Override
    public Exception requestFail(HttpRequest<?> httpRequest, Exception e) {
        return new HttpException(e.getMessage(), e);
    }

    @Override
    public Object readCache(HttpRequest<?> httpRequest, Type type, long cacheTime) {
        String cacheKey = GsonFactory.getSingletonGson().toJson(httpRequest.getRequestApi());
        String cacheValue = MmkvUtil.INSTANCE.getString(cacheKey, null);
        if (cacheValue == null || "".equals(cacheValue) || "{}".equals(cacheValue)) {
            return null;
        }
        return GsonFactory.getSingletonGson().fromJson(cacheValue, type);
    }

    @Override
    public boolean writeCache(HttpRequest<?> httpRequest, Response response, Object result) {
        String cacheKey = GsonFactory.getSingletonGson().toJson(httpRequest.getRequestApi());
        String cacheValue = GsonFactory.getSingletonGson().toJson(result);
        if (cacheValue == null || "".equals(cacheValue) || "{}".equals(cacheValue)) {
            return false;
        }
        MmkvUtil.INSTANCE.putString(cacheKey, cacheValue);
        return true;
    }
}