package com.jintao.vipmanager.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.bean.UserInfo
import com.jintao.vipmanager.databinding.ActivityLoginBinding
import com.jintao.vipmanager.dialog.PermDeniedDialog
import com.jintao.vipmanager.listener.PermissionCallback
import com.jintao.vipmanager.utils.GeneralUtils
import com.jintao.vipmanager.utils.LoginManager
import com.jintao.vipmanager.utils.OpenSettingUtil
import com.jintao.vipmanager.utils.PermissionsUtil
import com.lxj.xpopup.interfaces.OnConfirmListener

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private var mPermDeniedDialog:PermDeniedDialog? = null
    private var isOpenPermPage = false
    private var isOpenStorageManager = false

    override fun initData() {
        checkAndRequestPerm()
    }

    override fun initListener() {
        mBinding.stvSubmitUser.setOnClickListener {
            if (GeneralUtils.getInstence().onClickEnable()) {
                val inputName = mBinding.etLoginName.editableText.toString()
                val inputPhoneNumber = mBinding.etLoginPhone.editableText.toString()
                if (!TextUtils.isEmpty(inputName)) {
                    if (!TextUtils.isEmpty(inputPhoneNumber)) {
                        if (inputPhoneNumber.length==11) {
                            loginUser(inputName,inputPhoneNumber)
                        }else {
                            ToastUtils.show("请输入正确的手机号")
                        }
                    }else {
                        ToastUtils.show("请输入手机号")
                    }
                }else {
                    ToastUtils.show("请输入商家名称")
                }
            }
        }
    }

    override fun initObserve() {

    }

    private fun loginUser(inputName: String,inputPhoneNumber: String) {
        val userInfo = UserInfo()
        userInfo.isLogin = true
        userInfo.userName = inputName
        userInfo.phoneNumber = inputPhoneNumber
        userInfo.sign = System.currentTimeMillis().toString()
        LoginManager.getInstence().saveLoginInfo(userInfo)
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (isOpenPermPage) {
            isOpenPermPage = false
            checkAndRequestPerm()
        }else {
            if (isOpenStorageManager) {
                isOpenStorageManager = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R&&!Environment.isExternalStorageManager()) {
                    isOpenStorageManager = true
                    GeneralUtils.getInstence().goManagerFileAccess(this@LoginActivity)
                }else {
                    val loginInfo = LoginManager.getInstence().getLoginInfo()
                    if (loginInfo.isLogin) {
                        startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun checkAndRequestPerm() {
        PermissionsUtil.getInstence().checkAndRequestPerm(this,0,object : PermissionCallback{
            override fun onPermissionsResult(requestCode: Int, permStatus: Int) {
                if (permStatus==PermissionsUtil.GRANT) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R&&!Environment.isExternalStorageManager()) {
                        if (!isOpenStorageManager) {
                            isOpenStorageManager = true
                            GeneralUtils.getInstence().goManagerFileAccess(this@LoginActivity)
                        }
                    }else {
                        val loginInfo = LoginManager.getInstence().getLoginInfo()
                        if (loginInfo.isLogin) {
                            startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                            finish()
                        }
                    }
                }else {
                    val title = "开启设备权限"
                    val desc = "为保证功能正常使用，请开启存储权限"
                    if (mPermDeniedDialog==null) {
                        mPermDeniedDialog = PermDeniedDialog(this@LoginActivity,title,desc,object :
                            OnConfirmListener {
                            override fun onConfirm() {
                                isOpenPermPage = true
                                OpenSettingUtil.openSettingPage(this@LoginActivity,0)
                            }
                        })
                        mPermDeniedDialog!!.show()
                    }else {
                        mPermDeniedDialog!!.show()
                    }
                }
            }

        }, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }
}