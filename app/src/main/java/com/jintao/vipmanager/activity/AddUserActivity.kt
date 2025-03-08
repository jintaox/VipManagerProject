package com.jintao.vipmanager.activity

import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.lifecycleScope
import com.hjq.gson.factory.GsonFactory
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.MyApplication
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.bean.UserOperateRecordInfo
import com.jintao.vipmanager.database.DatabaseRepository
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.launchWithNotLoadingFlow
import com.jintao.vipmanager.databinding.ActivityAddUserBinding
import com.jintao.vipmanager.utils.AppConstant
import com.jintao.vipmanager.utils.GeneralUtils
import com.jintao.vipmanager.utils.MmkvUtil
import com.jintao.vipmanager.utils.StreamUtils
import kotlinx.coroutines.launch

class AddUserActivity : BaseActivity<ActivityAddUserBinding>() {

    private lateinit var databaseRepository:DatabaseRepository;

    override fun initData() {
        mBinding.title.tvTitleContent.setText("增加会员用户")
        databaseRepository = DatabaseRepository()
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

    override fun initObserve() {

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
        launchWithNotLoadingFlow({ databaseRepository.queryByVipPhoneAvai(inputPhone) }) {
            onSuccess = { isPhoneAvai ->
                if (isPhoneAvai) {
                    if (!TextUtils.isEmpty(inputAmount)) {
                        try {
                            val saveAmount = inputAmount.toFloat()
                            saveUserData(inputName,inputPhone,saveAmount)
                        }catch (e:NumberFormatException) {
                            ToastUtils.show("请输入正确的金额")
                        }
                    }else {
                        saveUserData(inputName,inputPhone,0f)
                    }
                }else {
                    ToastUtils.show("用户已存在")
                    var intent = Intent()
                    intent.putExtra("addpage_phone_number",inputPhone)
                    setResult(201,intent)
                    finish()
                }
            }
        }
    }

    private fun saveUserData(inputName: String, inputPhone: String, thisAmount: Float) {
        showLoadingDialog("正在保存...")
        val checkedNan = mBinding.rbNanSelect.isChecked
        val checkedNv = mBinding.rbNvSelect.isChecked
        launchWithNotLoadingFlow({ databaseRepository.getNewVipUid(inputPhone) }) {
            onSuccess = { generateUid ->
                val currentTime = System.currentTimeMillis()

                val jifenStr = MmkvUtil.getString(AppConstant.INTEGRAL_CONVERT_NUMBER, "1")
                val saveJifen = jifenStr.toFloat()
                var thisJifen = thisAmount * saveJifen

                val dbVipUserInfo = DbVipUserInfo()

                dbVipUserInfo.setUserName(inputName)
                dbVipUserInfo.setPhoneNumber(inputPhone)
                dbVipUserInfo.setUid(generateUid)
                if (checkedNan) {
                    dbVipUserInfo.setUserSex("男")
                }else {
                    dbVipUserInfo.setUserSex("女")
                }

                dbVipUserInfo.setExchangeNumber(thisAmount)
                dbVipUserInfo.setTotalAmount(thisAmount)
                dbVipUserInfo.setCurrentAmount(thisAmount)
                dbVipUserInfo.setUserIntegral(thisJifen)
                dbVipUserInfo.setTotalIntegral(thisJifen)
                dbVipUserInfo.setConsumeTime(currentTime)
                dbVipUserInfo.setRegisterTime(GeneralUtils.getInstence().timeFormat(currentTime))
                dbVipUserInfo.setConsumeNumber(if(thisAmount==0f) 0 else 1)

                lifecycleScope.launch {
                    databaseRepository.getVipUserDao().insertUserInfo(dbVipUserInfo)
                }

                val userOperateRecord1 = UserOperateRecordInfo()
                userOperateRecord1.uid = generateUid
                userOperateRecord1.userName = inputName
                userOperateRecord1.phoneNumber = inputPhone
                val content = "新增用户"+dbVipUserInfo.getUserSex()
                userOperateRecord1.eventName = content
                userOperateRecord1.createTime = dbVipUserInfo.getRegisterTime()
                MyApplication.cacheOperateRecordList.add(0, userOperateRecord1)

                if(thisAmount!=0f) {
                    val dbUserConsumeInfo = DbUserConsumeInfo()
                    dbUserConsumeInfo.setConsumeTime(GeneralUtils.getInstence().timeFormat(currentTime))
                    dbUserConsumeInfo.setConsumeAmount(thisAmount)
                    dbUserConsumeInfo.setConsumeIntegral(thisJifen)
                    dbUserConsumeInfo.setConverRatio("1元/"+jifenStr+"积分")
                    dbUserConsumeInfo.setContent("新入会员")
                    dbUserConsumeInfo.setConsumeType(1)
                    dbUserConsumeInfo.setUid(generateUid)

                    lifecycleScope.launch {
                        databaseRepository.getUserConsumeDao().insertConsumeInfo(dbUserConsumeInfo)
                    }

                    MyApplication.todayCount ++
                    MyApplication.todayConsume += thisAmount

                    if (thisAmount > 15) {
                        val userOperateRecord2 = UserOperateRecordInfo()
                        userOperateRecord2.uid = generateUid
                        userOperateRecord2.userName = inputName
                        userOperateRecord2.phoneNumber = inputPhone
                        val content = "增加消费"+thisAmount.toString()
                        userOperateRecord2.eventName = content
                        userOperateRecord2.createTime = dbUserConsumeInfo.getConsumeTime()
                        MyApplication.cacheOperateRecordList.add(0, userOperateRecord2)
                    }
                }

                val backupFilePath = StreamUtils.getBackupFilePath(this@AddUserActivity)
                if (!TextUtils.isEmpty(backupFilePath)) {
                    MyApplication.uploadBackupCount = 0
                    val recordJson = GsonFactory.getSingletonGson().toJson(MyApplication.cacheOperateRecordList)
                    StreamUtils.writeFile(backupFilePath, recordJson)
                }
                mBinding.stvSubmitUser.postDelayed({
                    dismissLoadingDialog()
                    setResult(200)
                    finish()
                },300)
            }
        }
    }

    override fun getViewBinding(): ActivityAddUserBinding {
        return ActivityAddUserBinding.inflate(layoutInflater)
    }
}