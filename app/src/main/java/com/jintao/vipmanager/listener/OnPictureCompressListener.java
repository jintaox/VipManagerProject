package com.jintao.vipmanager.listener;

/**
 * Author: jintao
 * CreateDate: 2023/8/5 17:26
 * Description:
 */
public interface OnPictureCompressListener {
    void onResultSuccess(String picturePath);
    void onResultFail();
}
