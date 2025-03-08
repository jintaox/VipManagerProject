package com.jintao.vipmanager.activity

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import com.hjq.toast.ToastUtils
import com.jintao.secret.EncrypyUtils
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.databinding.ActivitySetPasswordBinding
import com.jintao.vipmanager.utils.AppConstant
import com.jintao.vipmanager.utils.MmkvUtil

/**
 * root密码：68013975
 */
class SetPasswordActivity : BaseActivity<ActivitySetPasswordBinding>() {

    private var savePwd = ""
    private var rootPwd = ""

    override fun initData() {
        mBinding.title.tvTitleContent.setText("修改管理员密码")
        savePwd = MmkvUtil.getString(AppConstant.ADMIN_PASSWORD_ID, "2580")

        rootPwd = EncrypyUtils().decode("QAmBq3EHvPbDG9XYOlVecg==")
    }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener { finish() }
        mBinding.stvSubmitUser.setOnClickListener {
            val inputOldPwd = mBinding.etInputOldpwd.editableText.trim().toString()
            val inputNewPwd = mBinding.etInputNewpwd.editableText.trim().toString()
            val inputCfPwd = mBinding.etInputCfpwd.editableText.trim().toString()
            sheckInput(inputOldPwd, inputNewPwd, inputCfPwd)
        }

        mBinding.etInputRoot.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(str: Editable?) {
                val input = str.toString()
                if (input.equals(rootPwd)) {
                    MmkvUtil.putString(AppConstant.ADMIN_PASSWORD_ID, "2580")
                    ToastUtils.show("密码重置成功")
                    finish()
                }
            }

        })
    }

    override fun initObserve() {

    }

    private fun sheckInput(inputOldPwd: String, inputNewPwd: String, inputCfPwd: String) {
        if (TextUtils.isEmpty(inputOldPwd)) {
            ToastUtils.show("请输入旧密码")
            return
        }
        if (TextUtils.isEmpty(inputNewPwd)) {
            ToastUtils.show("请输入新密码")
            return
        }
        if (TextUtils.isEmpty(inputCfPwd)) {
            ToastUtils.show("请再次输入新密码")
            return
        }
        if (inputNewPwd.length < 4 || inputCfPwd.length < 4) {
            ToastUtils.show("密码长度不能少于4位")
            return
        }

        if (!inputNewPwd.equals(inputCfPwd)) {
            ToastUtils.show("两次新密码输入不一致")
            return
        }

        if (inputOldPwd.equals(savePwd)) {
            MmkvUtil.putString(AppConstant.ADMIN_PASSWORD_ID, inputNewPwd)
            ToastUtils.show("密码修改成功")
            finish()
        } else {
            ToastUtils.show("旧密码错误")
        }
    }

    override fun getViewBinding(): ActivitySetPasswordBinding {
        return ActivitySetPasswordBinding.inflate(layoutInflater)
    }
}