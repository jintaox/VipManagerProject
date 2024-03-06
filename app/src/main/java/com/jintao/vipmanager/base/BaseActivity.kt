package com.jintao.vipmanager.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.jintao.vipmanager.dialog.LoadingDialog
import com.jintao.vipmanager.utils.GeneralUtils
import com.gyf.immersionbar.ImmersionBar
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.utils.PermissionsUtil
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView

abstract class BaseActivity<T : ViewBinding>: AppCompatActivity() {

    lateinit var mBinding: T
    private var loadingPopupView: BasePopupView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarConfig().init()

        mBinding = getViewBinding()
        setContentView(mBinding.root)
        initListener()
        initData()
    }

    /**
     * 初始化沉浸式状态栏
     */
    protected open fun setStatusBarConfig(): ImmersionBar {
        return ImmersionBar.with(this)
            .statusBarColor(setStatusBarColor())
            .navigationBarColor(setNavigationBarColor())
            .statusBarDarkFont(setStatusBarDarkFont())
            .navigationBarDarkIcon(setNavigationBarDarkIcon())
            .fitsSystemWindows(true)//解决布局和状态栏重叠
            .autoDarkModeEnable(false)
    }

    protected open fun setStatusBarColor():String {
        return "#FFFFFF"
    }

    protected open fun setNavigationBarColor():String {
        return "#FFFFFF"
    }

    protected open fun setStatusBarDarkFont():Boolean {
        return true
    }

    protected open fun setNavigationBarDarkIcon():Boolean {
        return true
    }

    abstract fun initData()

    abstract fun initListener()

    abstract fun getViewBinding(): T

    fun showLoadingDialog(text:String = "请稍等...") {
        if (loadingPopupView==null||!loadingPopupView!!.isShow) {
            loadingPopupView = XPopup.Builder(this)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .autoFocusEditText(false)
                .isRequestFocus(false)
                .hasShadowBg(false)
                .asCustom(LoadingDialog(this,text))
                .show()
        }
    }

    fun dismissLoadingDialog() {
        loadingPopupView?.let {
            it.dismiss()
        }
    }

    fun setErrorMsg(message:String) {
        ToastUtils.show(message)
    }

    fun setNetworkAbnormal() {
        if (GeneralUtils.getInstence().networkAvailable(this)) {
            ToastUtils.show("网络连接失败，请重试")
        }else {
            ToastUtils.show("无法连接网络，请检查网络设置")
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        PermissionsUtil.getInstence().onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager? =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        if (event.getAction() === MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null) {
                if (getCurrentFocus()?.getWindowToken() != null) {
                    imm?.hideSoftInputFromWindow(
                        getCurrentFocus()?.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
            }
        }
        return super.onTouchEvent(event)
    }
}