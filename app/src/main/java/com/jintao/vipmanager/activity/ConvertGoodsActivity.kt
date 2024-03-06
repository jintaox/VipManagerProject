package com.jintao.vipmanager.activity

import android.text.TextUtils
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.database.bean.DbConvertGoodsInfo
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.helper.DbVipUserHelper
import com.jintao.vipmanager.databinding.ActivityConvertGoodsBinding
import com.jintao.vipmanager.utils.*

class ConvertGoodsActivity : BaseActivity<ActivityConvertGoodsBinding>() {

    private lateinit var vipUserDao: DbVipUserHelper<DbVipUserInfo>
    private lateinit var convertGoodsDao: DbVipUserHelper<DbConvertGoodsInfo>
    private lateinit var vipUserInfo:DbVipUserInfo

    override fun initData() {
        var vipUid = intent.getLongExtra(AppConstant.VIP_USER_ID, 0)
        val userName = intent.getStringExtra("vip_user_name")
        val phoneNumber = intent.getStringExtra("vip_phone_number")

        vipUserDao = DbVipUserHelper(this, DbVipUserInfo::class.java).vipUserDao
        convertGoodsDao = DbVipUserHelper(this, DbConvertGoodsInfo::class.java).convertGoodsDao

        vipUserInfo = vipUserDao.queryById(vipUid)
        mBinding.title.tvTitleContent.setText("积分兑换")
        mBinding.tvUserName.setText(userName)
        mBinding.tvUserPhone.setText(phoneNumber)
        mBinding.tvUserJifen.setText(GeneralUtils.getInstence().formatAmount(vipUserInfo.userIntegral))
    }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener { finish() }
        mBinding.stvSubmitUser.setOnClickListener {
            val inputJifen = mBinding.etInputJifen.editableText.toString()
            val inputContent = mBinding.etInputContent.editableText.toString()
            checkInputResult(inputJifen,inputContent)
        }
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
            if (vipUserInfo.userIntegral - thisJifen >= 0) {
                val counterAmount = thisJifen / jifenSaveBl

                val resultAmount = CalculatorUtils.getInstence().subtract(vipUserInfo.currentAmount,counterAmount)
                val resultIntegral = CalculatorUtils.getInstence().subtract(vipUserInfo.userIntegral,thisJifen)

                if (resultAmount.toFloat() <= 0f) {
                    vipUserInfo.currentAmount = 0f
                }else {
                    vipUserInfo.currentAmount = resultAmount.toFloat()
                }
                vipUserInfo.userIntegral = resultIntegral.toFloat()
                vipUserInfo.consumeTime = currentTime
                vipUserDao.update(vipUserInfo)

                val dbConvertGoodsInfo = DbConvertGoodsInfo()
                dbConvertGoodsInfo.uid = vipUserInfo.uid
                dbConvertGoodsInfo.time = GeneralUtils.getInstence().timeFormat(currentTime)
                dbConvertGoodsInfo.desc = inputContent
                dbConvertGoodsInfo.useIntegral = thisJifen
                convertGoodsDao.insert(dbConvertGoodsInfo)

                ToastUtils.show("兑换成功")
                setResult(200)
                finish()
            }else {
                ToastUtils.show("兑换积分不能大于可用积分")
            }
        }catch (e:NumberFormatException) {
            ToastUtils.show("请输入正确的积分")
        }
    }

    override fun getViewBinding(): ActivityConvertGoodsBinding {
        return ActivityConvertGoodsBinding.inflate(layoutInflater)
    }

}