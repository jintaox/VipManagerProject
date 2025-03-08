package com.jintao.vipmanager.activity

import android.text.TextUtils
import androidx.lifecycle.lifecycleScope
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.database.DatabaseRepository
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.dao.DbVipUserInfoDao
import com.jintao.vipmanager.database.launchWithNotLoadingFlow
import com.jintao.vipmanager.databinding.ActivityChangeVipInfoBinding
import com.jintao.vipmanager.utils.AppConstant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChangeVipInfoActivity : BaseActivity<ActivityChangeVipInfoBinding>() {

    private var vipUserId:Long = -1
    private lateinit var databaseRepository: DatabaseRepository
    private lateinit var vipUserDao: DbVipUserInfoDao
    private lateinit var vipUserInfo:DbVipUserInfo

    override fun initData() {
        mBinding.title.tvTitleContent.setText("修改信息")
        vipUserId = intent.getLongExtra(AppConstant.VIP_USER_ID, -1)
        databaseRepository = DatabaseRepository()
        vipUserDao = databaseRepository.getVipUserDao()

        launchWithNotLoadingFlow({ vipUserDao.queryUserInfoFromId(vipUserId) }) {
            onSuccess = { result ->
                vipUserInfo = result
                mBinding.etUserName.setText(vipUserInfo.getUserName())
                mBinding.etInputPhone.setText(vipUserInfo.getPhoneNumber())
                if (vipUserInfo.getUserSex().equals("女")) {
                    mBinding.rbNvSelect.isChecked = true
                    mBinding.rbNanSelect.isChecked = false
                }
            }
        }
    }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener {
            finish()
        }

        mBinding.stvSubmitUser.setOnClickListener {
            val inputName = mBinding.etUserName.editableText.toString()
            val inputPhone = mBinding.etInputPhone.editableText.toString()

            checkUpdateUsetData(inputName,inputPhone)
        }
    }

    private fun checkUpdateUsetData(inputName: String, inputPhone: String) {
        if (TextUtils.isEmpty(inputName)) {
            ToastUtils.show("请输入姓名")
            return
        }
        if (TextUtils.isEmpty(inputPhone)) {
            ToastUtils.show("请输入手机号")
            return
        }
        if (inputPhone.length < 5) {
            ToastUtils.show("号码最少为6位数字")
            return
        }
        if (inputPhone.equals(vipUserInfo.getPhoneNumber())) {
            showLoadingDialog("正在保存...")
            saveUserData(inputName,inputPhone)
        }else {
            launchWithNotLoadingFlow({ databaseRepository.queryByVipPhoneAvai(inputPhone) }) {
                onSuccess = { isPhoneAvai ->
                    if (isPhoneAvai) {
                        showLoadingDialog("正在保存...")
                        saveUserData(inputName,inputPhone)
                    }else {
                        ToastUtils.show("手机号已存在")
                    }
                }
            }
        }
    }

    override fun initObserve() {

    }

    private fun saveUserData(inputName: String, inputPhone: String) {
        val checkedNan = mBinding.rbNanSelect.isChecked
        val checkedNv = mBinding.rbNvSelect.isChecked
        vipUserInfo.setUserName(inputName)
        vipUserInfo.setPhoneNumber(inputPhone)
        if (checkedNan) {
            vipUserInfo.setUserSex("男")
        }else {
            vipUserInfo.setUserSex("女")
        }
        lifecycleScope.launch {
            vipUserDao.updateUserInfo(vipUserInfo)

            delay(300)
            dismissLoadingDialog()
            ToastUtils.show("保存成功")
            setResult(200)
            finish()
        }
    }

    override fun getViewBinding(): ActivityChangeVipInfoBinding {
        return ActivityChangeVipInfoBinding.inflate(layoutInflater)
    }
}