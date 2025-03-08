package com.jintao.vipmanager.base

import androidx.lifecycle.LifecycleOwner

interface IUiView : LifecycleOwner {

    fun showLoadingDialog(text:String = "请稍等...")

    fun dismissLoadingDialog()
}