package com.jintao.vipmanager.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.dialog.LoadingDialog
import com.jintao.vipmanager.utils.GeneralUtils
import com.jintao.vipmanager.utils.PermissionsUtil

abstract class BaseActivity<T : ViewBinding>: AppCompatActivity(), IUiView {

    lateinit var mBinding: T
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarConfig().init()

        mBinding = getViewBinding()
        setContentView(mBinding.root)
        initListener()
        initObserve()
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

    abstract fun getViewBinding(): T
    abstract fun initListener()
    abstract fun initObserve()
    abstract fun initData()

    override fun showLoadingDialog(content:String) {
        if (loadingDialog == null || !loadingDialog!!.isShowing()) {
            loadingDialog = LoadingDialog(this, content)
            loadingDialog!!.show()
        }
    }

    override fun dismissLoadingDialog() {
        if (!isFinishing && loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
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