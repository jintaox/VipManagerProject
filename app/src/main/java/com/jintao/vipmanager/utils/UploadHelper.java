package com.jintao.vipmanager.utils;

import android.text.TextUtils;
import android.util.Log;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.drhkj.pialn.listener.OnPictureUploadListener;
import com.jintao.vipmanager.MyApplication;
import com.jintao.vipmanager.bean.UserInfo;

public class UploadHelper {

    private static OSS getOSSClient(String accessKeyId, String accessKeySecret) {
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(5 * 1000); // connction time out default 15s
        conf.setSocketTimeout(5 * 1000); // socket timeout，default 15s
        conf.setMaxConcurrentRequest(5); // synchronous request number，default 5
        conf.setMaxErrorRetry(2); // retry，default 2
        OSSCredentialProvider credentialProvider =
                new OSSPlainTextAKSKCredentialProvider(accessKeyId,
                        accessKeySecret);
        return new OSSClient(MyApplication.Companion.getMContext(), AppConfig.aliyunEndpoint, credentialProvider,conf);
    }

    /**
     * 上传普通图片
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static void uploadImage(String path,OnPictureUploadListener listener) {
        String aliyunEndpoint = AppConfig.aliyunEndpoint;
        String accessKeyId = AppConfig.aliyunKeyId;
        String accessKeySecret = AppConfig.aliyunKeySecret;

        if (TextUtils.isEmpty(aliyunEndpoint)||TextUtils.isEmpty(accessKeyId)||TextUtils.isEmpty(accessKeySecret)) {
            listener.onResultFail();
            return;
        }
        String objectKey = getObjectImagcabseKey();
        // 构造上传请求
        PutObjectRequest request = new PutObjectRequest(AppConfig.aliyunBucketName, objectKey, path);
        //获取仓库初始化实例
        OSS client = getOSSClient(accessKeyId,accessKeySecret);
        //异步上传请求
        OSSAsyncTask<PutObjectResult> task = client.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                /********************  当前异步线程，展示UI请切换UI线程    *********************/
                //获取解析url
                String url = client.presignPublicObjectURL(AppConfig.aliyunBucketName, objectKey);
                listener.onResultSuccess(url);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                /********************  当前异步线程，展示UI请切换UI线程    *********************/
                listener.onResultFail();
            }
        });
    }

    public static void uploadFile(String path,String fileName,OnPictureUploadListener listener) {
        String aliyunEndpoint = AppConfig.aliyunEndpoint;
        String accessKeyId = AppConfig.aliyunKeyId;
        String accessKeySecret = AppConfig.aliyunKeySecret;

        if (TextUtils.isEmpty(aliyunEndpoint)||TextUtils.isEmpty(accessKeyId)||TextUtils.isEmpty(accessKeySecret)) {
            listener.onResultFail();
            return;
        }
        String objectKey = getObjectImagcabseKey(fileName);
        // 构造上传请求
        PutObjectRequest request = new PutObjectRequest(AppConfig.aliyunBucketName, objectKey, path);
        //获取仓库初始化实例
        OSS client = getOSSClient(accessKeyId,accessKeySecret);
        //异步上传请求
        OSSAsyncTask<PutObjectResult> task = client.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                /********************  当前异步线程，展示UI请切换UI线程    *********************/
                //获取解析url
                String url = client.presignPublicObjectURL(AppConfig.aliyunBucketName, objectKey);
                listener.onResultSuccess(url);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                /********************  当前异步线程，展示UI请切换UI线程    *********************/
                listener.onResultFail();
            }
        });
    }

    /**
     * 返回key
     *
     * @return key
     */
    private static String getObjectImagcabseKey() {
        UserInfo loginInfo = LoginManager.Companion.getInstence().getLoginInfo();
        String name = loginInfo.getPhoneNumber()+"/"+System.currentTimeMillis() + ".db";
        return name;
    }

    private static String getObjectImagcabseKey(String fileName) {
        UserInfo loginInfo = LoginManager.Companion.getInstence().getLoginInfo();
        String name = loginInfo.getPhoneNumber()+"/"+fileName;
        return name;
    }
}
