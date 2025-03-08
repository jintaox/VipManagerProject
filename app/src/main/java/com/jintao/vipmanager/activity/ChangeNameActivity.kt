package com.jintao.vipmanager.activity

import android.text.TextUtils
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.databinding.ActivityChangeNameBinding
import com.jintao.vipmanager.utils.GeneralUtils
import com.jintao.vipmanager.utils.LoginManager

class ChangeNameActivity : BaseActivity<ActivityChangeNameBinding>() {

    override fun initData() {
        mBinding.title.tvTitleContent.setText("更改名称")
    }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener {
            finish()
        }
        mBinding.stvSubmitUser.setOnClickListener {
            if (GeneralUtils.getInstence().onClickEnable()) {
                val userInfo = LoginManager.getInstence().getLoginInfo()
                val inputName = mBinding.etLoginName.editableText.toString()
                if (!TextUtils.isEmpty(inputName)) {
                    if (!userInfo.userName.equals(inputName)) {
                        userInfo.userName = inputName
                        LoginManager.getInstence().saveLoginInfo(userInfo)
                        setResult(200)
                        finish()
                    }else {//如果和之前相同
                        finish()
                    }
                }else {
                    ToastUtils.show("请输入商家名称")
                }
            }
        }
    }

    override fun initObserve() {

    }

    override fun getViewBinding(): ActivityChangeNameBinding {
        return ActivityChangeNameBinding.inflate(layoutInflater)
    }
}