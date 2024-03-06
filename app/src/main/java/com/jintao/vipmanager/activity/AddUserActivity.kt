package com.jintao.vipmanager.activity

import android.content.Intent
import android.text.TextUtils
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.MyApplication
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.helper.DbVipUserHelper
import com.jintao.vipmanager.databinding.ActivityAddUserBinding
import com.jintao.vipmanager.utils.AppConstant
import com.jintao.vipmanager.utils.GeneralUtils
import com.jintao.vipmanager.utils.MmkvUtil

class AddUserActivity : BaseActivity<ActivityAddUserBinding>() {

    private lateinit var vipUserDao:DbVipUserHelper<DbVipUserInfo>
    private lateinit var consumeUserDao:DbVipUserHelper<DbUserConsumeInfo>

    override fun initData() {
        mBinding.title.tvTitleContent.setText("增加会员用户")
        vipUserDao = DbVipUserHelper(this, DbVipUserInfo::class.java).vipUserDao
        consumeUserDao = DbVipUserHelper(this, DbUserConsumeInfo::class.java).userConsumeDao
    }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener {
            finish()
        }
        mBinding.stvSubmitUser.setOnClickListener {
            if (GeneralUtils.getInstence().onClickEnable()) {
                val inputName = mBinding.etInputName.editableText.toString()
                val inputPhone = mBinding.etInputPhone.editableText.toString()
                val inputAmount = mBinding.etInputAmount.editableText.toString()

                checkInputResult(inputName,inputPhone,inputAmount)
            }
        }
    }

    private fun checkInputResult(inputName: String, inputPhone: String, inputAmount: String) {
        if (TextUtils.isEmpty(inputName)) {
            ToastUtils.show("请输入用户姓名")
            return
        }
        if (TextUtils.isEmpty(inputPhone)) {
            ToastUtils.show("请输入用户手机号")
            return
        }
        if (inputPhone.length < 4) {
            ToastUtils.show("号码最少为4位数字")
            return
        }
        val isPhoneAvai = vipUserDao.queryByVipPhoneAvai(inputPhone)
        if (isPhoneAvai) {
            var saveAmount = 0f
            if (!TextUtils.isEmpty(inputAmount)) {
                try {
                    saveAmount = inputAmount.toFloat()
                }catch (e:NumberFormatException) {
                    ToastUtils.show("请输入正确的金额")
                    return
                }
            }
            showLoadingDialog("正在保存...")
            saveUserData(inputName,inputPhone,saveAmount)
        }else {
            ToastUtils.show("用户已存在")
            var intent = Intent()
            intent.putExtra("addpage_phone_number",inputPhone)
            setResult(201,intent)
            finish()
        }
    }

    private fun saveUserData(inputName: String, inputPhone: String, thisAmount: Float) {
        val checkedNan = mBinding.rbNanSelect.isChecked
        val checkedNv = mBinding.rbNvSelect.isChecked
        Thread{
            var generateUid = GeneralUtils.getInstence().generateRandomUid(inputPhone).toInt()
            val queryByVipUidAvai = vipUserDao.queryByVipUidAvai(generateUid)
            if (!queryByVipUidAvai) {
                generateUid = GeneralUtils.getInstence().generateRandomUid(inputPhone).toInt()
            }
            val currentTime = System.currentTimeMillis()

            val jifenStr = MmkvUtil.getString(AppConstant.INTEGRAL_CONVERT_NUMBER, "1")
            val saveJifen = jifenStr.toFloat()
            var thisJifen = thisAmount * saveJifen

            val dbVipUserInfo = DbVipUserInfo()

            dbVipUserInfo.userName = inputName
            dbVipUserInfo.phoneNumber = inputPhone
            dbVipUserInfo.uid = generateUid
            if (checkedNan) {
                dbVipUserInfo.userSex = "男"
            }else {
                dbVipUserInfo.userSex = "女"
            }

            dbVipUserInfo.lastAmount = thisAmount
            dbVipUserInfo.totalAmount = thisAmount
            dbVipUserInfo.currentAmount = thisAmount
            dbVipUserInfo.userIntegral = thisJifen
            dbVipUserInfo.totalIntegral = thisJifen
            dbVipUserInfo.consumeTime = currentTime
            dbVipUserInfo.registerTime = GeneralUtils.getInstence().timeFormat(currentTime)
            dbVipUserInfo.consumeNumber = if(thisAmount==0f) 0 else 1
            vipUserDao.insert(dbVipUserInfo)

            if(thisAmount!=0f) {
                val dbUserConsumeInfo = DbUserConsumeInfo()
                dbUserConsumeInfo.consumeTime  =  GeneralUtils.getInstence().timeFormat(currentTime)
                dbUserConsumeInfo.consumeAmount = thisAmount
                dbUserConsumeInfo.consumeIntegral = thisJifen
                dbUserConsumeInfo.converRatio = "1元/"+jifenStr+"积分"
                dbUserConsumeInfo.content = "新入会员"
                dbUserConsumeInfo.consumeType = true
                dbUserConsumeInfo.uid = generateUid

                consumeUserDao.insert(dbUserConsumeInfo)

                MyApplication.todayCount ++
                MyApplication.todayConsume += thisAmount
            }

            runOnUiThread {
                dismissLoadingDialog()
                setResult(200)
                finish()
            }
        }.start()
    }

    override fun getViewBinding(): ActivityAddUserBinding {
        return ActivityAddUserBinding.inflate(layoutInflater)
    }
}