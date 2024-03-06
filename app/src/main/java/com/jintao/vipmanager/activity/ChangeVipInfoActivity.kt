package com.jintao.vipmanager.activity

import android.text.TextUtils
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.helper.DbVipUserHelper
import com.jintao.vipmanager.databinding.ActivityChangeVipInfoBinding
import com.jintao.vipmanager.utils.AppConstant

class ChangeVipInfoActivity : BaseActivity<ActivityChangeVipInfoBinding>() {

    private var vipUserId:Long = -1
    private lateinit var vipUserDao: DbVipUserHelper<DbVipUserInfo>
    private lateinit var vipUserInfo:DbVipUserInfo

    override fun initData() {
        mBinding.title.tvTitleContent.setText("修改信息")
        vipUserId = intent.getLongExtra(AppConstant.VIP_USER_ID, -1)
        vipUserDao = DbVipUserHelper(this, DbVipUserInfo::class.java).vipUserDao
        vipUserInfo = vipUserDao.queryById(vipUserId)

        mBinding.etUserName.setText(vipUserInfo.userName)
        mBinding.etInputPhone.setText(vipUserInfo.phoneNumber)
        if (vipUserInfo.userSex.equals("女")) {
            mBinding.rbNvSelect.isChecked = true
            mBinding.rbNanSelect.isChecked = false
        }
    }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener {
            finish()
        }

        mBinding.stvSubmitUser.setOnClickListener {
            val inputName = mBinding.etUserName.editableText.toString()
            val inputPhone = mBinding.etInputPhone.editableText.toString()
            if (!TextUtils.isEmpty(inputName)) {
                if (!TextUtils.isEmpty(inputPhone)) {
                    if (inputPhone.length>=4) {
                        if (inputPhone.equals(vipUserInfo.phoneNumber)) {
                            showLoadingDialog("正在保存...")
                            saveUserData(inputName,inputPhone)
                        }else {
                            val isPhoneAvai = vipUserDao.queryByVipPhoneAvai(inputPhone)
                            if (isPhoneAvai) {
                                showLoadingDialog("正在保存...")
                                saveUserData(inputName,inputPhone)
                            }else {
                                ToastUtils.show("用户已存在")
                            }
                        }
                    }else {
                        ToastUtils.show("号码最少为4位数字")
                    }
                }else {
                    ToastUtils.show("请输入手机号")
                }
            }else {
                ToastUtils.show("请输入姓名")
            }
        }
    }

    private fun saveUserData(inputName: String, inputPhone: String) {
        val checkedNan = mBinding.rbNanSelect.isChecked
        val checkedNv = mBinding.rbNvSelect.isChecked
        Thread{
            vipUserInfo.userName = inputName
            vipUserInfo.phoneNumber = inputPhone
            if (checkedNan) {
                vipUserInfo.userSex = "男"
            }else {
                vipUserInfo.userSex = "女"
            }
            vipUserDao.update(vipUserInfo)
            runOnUiThread {
                dismissLoadingDialog()
                ToastUtils.show("保存成功")
                setResult(200)
                finish()
            }
        }.start()

    }

    override fun getViewBinding(): ActivityChangeVipInfoBinding {
        return ActivityChangeVipInfoBinding.inflate(layoutInflater)
    }
}