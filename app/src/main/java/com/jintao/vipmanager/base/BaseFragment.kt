package com.jintao.vipmanager.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.jintao.vipmanager.dialog.LoadingDialog
import com.jintao.vipmanager.utils.GeneralUtils
import com.hjq.toast.ToastUtils

abstract  class BaseFragment<T : ViewBinding> :Fragment() {

    lateinit var mBinding: T
    lateinit var mBaseContext:Context
    private var loadingPopupView: BasePopupView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mBaseContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = getViewBinding(inflater,container)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        recoveryPageDate(savedInstanceState)
        initData()
    }

    abstract fun initData()

    abstract fun initListener()

    protected open fun recoveryPageDate(savedInstanceState: Bundle?) {

    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

    fun showLoadingDialog(text:String = "请稍等...") {
        if (loadingPopupView==null||!loadingPopupView!!.isShow) {
            loadingPopupView = XPopup.Builder(mBaseContext)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .autoFocusEditText(false)
                .isRequestFocus(false)
                .hasShadowBg(false)
                .asCustom(LoadingDialog(mBaseContext,text))
                .show()
        }
    }

    fun dismissLoadingDialog() {
        loadingPopupView?.let {
            it.dismiss()
        }
    }

    open fun setErrorMsg(message:String) {
        ToastUtils.show(message)
    }

    open fun setNetworkAbnormal() {
        if (GeneralUtils.getInstence().networkAvailable(mBaseContext)) {
            ToastUtils.show("网络连接失败，请重试")
        }else {
            ToastUtils.show("无法连接网络，请检查网络设置")
        }
    }
}