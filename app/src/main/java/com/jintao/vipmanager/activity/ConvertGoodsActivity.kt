package com.jintao.vipmanager.activity

import android.text.TextUtils
import androidx.lifecycle.lifecycleScope
import com.drhkj.pialn.listener.OnPictureUploadListener
import com.hjq.gson.factory.GsonFactory
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.MyApplication
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.bean.UserOperateRecordInfo
import com.jintao.vipmanager.database.DatabaseRepository
import com.jintao.vipmanager.database.bean.DbConvertGoodsInfo
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.launchWithNotLoadingFlow
import com.jintao.vipmanager.databinding.ActivityConvertGoodsBinding
import com.jintao.vipmanager.utils.*
import kotlinx.coroutines.launch

class ConvertGoodsActivity : BaseActivity<ActivityConvertGoodsBinding>() {

    private lateinit var vipUserInfo:DbVipUserInfo
    private lateinit var databaseRepository:DatabaseRepository

    override fun initData() {
        var vipUid = intent.getLongExtra(AppConstant.VIP_USER_ID, 0)
        val userName = intent.getStringExtra("vip_user_name")
        val phoneNumber = intent.getStringExtra("vip_phone_number")

        databaseRepository = DatabaseRepository()
        mBinding.title.tvTitleContent.setText("积分兑换")
        mBinding.tvUserName.setText(userName)
        mBinding.tvUserPhone.setText(phoneNumber)

        launchWithNotLoadingFlow({ databaseRepository.getVipUserDao().queryUserInfoFromId(vipUid) }) {
            onSuccess = { result ->
                vipUserInfo = result
                mBinding.tvUserJifen.setText(GeneralUtils.getInstence().formatAmount(vipUserInfo.getUserIntegral()))
            }
        }
    }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener { finish() }
        mBinding.stvSubmitUser.setOnClickListener {
            val inputJifen = mBinding.etInputJifen.editableText.toString()
            val inputContent = mBinding.etInputContent.editableText.toString()
            checkInputResult(inputJifen,inputContent)
        }
    }

    override fun initObserve() {

    }

    private fun checkInputResult(inputJifen: String, inputContent: String) {
        if (TextUtils.isEmpty(inputJifen)) {
            ToastUtils.show("请输入需要兑换的积分")
            return
        }
        try {
            val thisJifen = inputJifen.toInt()
            if (thisJifen <= 0) {
                ToastUtils.show("请输入正确的积分")
                return
            }
            val currentTime = System.currentTimeMillis()
            val jifenSaveBl = MmkvUtil.getString(AppConstant.INTEGRAL_CONVERT_NUMBER, "1").toFloat()
            if (vipUserInfo.getUserIntegral() - thisJifen >= 0) {
                showLoadingDialog()
                val counterAmount = thisJifen / jifenSaveBl

                val resultAmount = CalculatorUtils.getInstence().subtract(vipUserInfo.getCurrentAmount(),counterAmount)
                val resultIntegral = CalculatorUtils.getInstence().subtract(vipUserInfo.getUserIntegral(),thisJifen)

                if (resultAmount.toFloat() <= 0f) {
                    vipUserInfo.setCurrentAmount(0f)
                }else {
                    vipUserInfo.setCurrentAmount(resultAmount.toFloat())
                }
                vipUserInfo.setUserIntegral(resultIntegral.toFloat())
                vipUserInfo.setConsumeTime(currentTime)
                //兑换数据
                val dbConvertGoodsInfo = DbConvertGoodsInfo()
                dbConvertGoodsInfo.setUid(vipUserInfo.getUid())
                dbConvertGoodsInfo.setTime(GeneralUtils.getInstence().timeFormat(currentTime))
                dbConvertGoodsInfo.setDesc(inputContent)
                dbConvertGoodsInfo.setUseIntegral(thisJifen)

                lifecycleScope.launch {
                    databaseRepository.getVipUserDao().updateUserInfo(vipUserInfo)
                    databaseRepository.getConvertGoodsDao().insertGoodsInfo(dbConvertGoodsInfo)
                }

                val userOperateRecordInfo = UserOperateRecordInfo()
                userOperateRecordInfo.uid = vipUserInfo.getUid()
                userOperateRecordInfo.userName = vipUserInfo.getUserName()
                userOperateRecordInfo.phoneNumber = vipUserInfo.getPhoneNumber()
                val content = "兑换-"+inputContent + "-积分" + thisJifen.toString()
                userOperateRecordInfo.eventName = content
                userOperateRecordInfo.createTime = dbConvertGoodsInfo.getTime()
                MyApplication.cacheOperateRecordList.add(0, userOperateRecordInfo)

                uploadbackupFile()
            }else {
                ToastUtils.show("兑换积分不能大于可用积分")
            }
        }catch (e:NumberFormatException) {
            ToastUtils.show("请输入正确的积分")
        }
    }

    private fun uploadbackupFile() {
        val backupFilePath = StreamUtils.getBackupFilePath(this)
        if (!TextUtils.isEmpty(backupFilePath)) {
            val recordJson = GsonFactory.getSingletonGson().toJson(MyApplication.cacheOperateRecordList)
            StreamUtils.writeFile(backupFilePath,recordJson)
            UploadHelper.uploadFile(backupFilePath, AppConfig.BACKUP_DATA_NAME, object : OnPictureUploadListener {
                override fun onResultSuccess(path: String) {
                    setConvertResultSuccess()
                }

                override fun onResultFail() {
                    setConvertResultSuccess()
                }
            })
        }else {
            setConvertResultSuccess()
        }
    }

    private fun setConvertResultSuccess() {
        runOnUiThread {
            dismissLoadingDialog()
            ToastUtils.show("兑换成功")
            setResult(200)
            finish()
        }
    }

    override fun getViewBinding(): ActivityConvertGoodsBinding {
        return ActivityConvertGoodsBinding.inflate(layoutInflater)
    }

}