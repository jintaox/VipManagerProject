package com.jintao.vipmanager.dialog

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.MyApplication
import com.jintao.vipmanager.R
import com.jintao.vipmanager.bean.UserOperateRecordInfo
import com.jintao.vipmanager.database.DatabaseRepository
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo
import com.jintao.vipmanager.databinding.DialogConsumeConfirmBinding
import com.jintao.vipmanager.listener.OnIntegralChangeListener
import com.jintao.vipmanager.utils.AppConstant
import com.jintao.vipmanager.utils.CalculatorUtils
import com.jintao.vipmanager.utils.GeneralUtils
import com.jintao.vipmanager.utils.MmkvUtil
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.coroutines.launch

/**
 * Author: jintao
 * CreateDate: 2023/8/26 13:12
 * Description:
 */
class ConsumeConfirmDialog(
    var mContext: Context,
    var vipUserId: Long,
    var title: String,
    var type: Int,
    var thisAmount: Float,
    var listener: OnIntegralChangeListener
) : CenterPopupView(mContext) {

    override fun onCreate() {
        super.onCreate()
        val mBinding = DialogConsumeConfirmBinding.bind(contentView)

        mBinding.stvCancel.setOnClickListener {
            dismiss()
        }
        mBinding.tvTitle.setText(title)
        val formatAmount = GeneralUtils.getInstence().formatAmount(thisAmount)
        var content = "当前消费金额数目较大，为" + formatAmount + "元，确定添加吗?"
        if(type!=1) {
            content = "当前消费金额数目较大，为" + formatAmount + "元，确定减去吗?"
        }
        GeneralUtils.getInstence().setRichText(content, formatAmount, mBinding.tvTipDesc)
        mBinding.viewKongbai1.setOnClickListener { dismiss() }
        mBinding.viewKongbai2.setOnClickListener { dismiss() }
        mBinding.stvConfirm.setOnClickListener {
            lifecycleScope.launch {
                saveUserAmountInfo(type,thisAmount)
            }
        }
    }

    private suspend fun saveUserAmountInfo(type: Int, thisAmount: Float) {
        val databaseRepository = DatabaseRepository()
        val vipUserDao = databaseRepository.getVipUserDao()
        val vipUserInfo = vipUserDao.queryUserInfoFromId(vipUserId)

        val jifenStr = MmkvUtil.getString(AppConstant.INTEGRAL_CONVERT_NUMBER, "1")
        val thisJifen = thisAmount * jifenStr.toFloat()
        val consumeUserDao = databaseRepository.getUserConsumeDao()
        val currentTime = System.currentTimeMillis()
        if (type == 1) {
            val totalAmount = CalculatorUtils.getInstence().add(vipUserInfo.getTotalAmount(), thisAmount)
            val currentAmount =
                CalculatorUtils.getInstence().add(vipUserInfo.getCurrentAmount(), thisAmount)
            val totalIntegral =
                CalculatorUtils.getInstence().add(vipUserInfo.getUserIntegral(), thisJifen)
            val userIntegral =
                CalculatorUtils.getInstence().add(vipUserInfo.getUserIntegral(), thisJifen)

            vipUserInfo.setExchangeNumber(thisAmount)
            vipUserInfo.setTotalAmount(totalAmount.toFloat())
            vipUserInfo.setCurrentAmount(currentAmount.toFloat())
            vipUserInfo.setTotalIntegral(totalIntegral.toFloat())
            vipUserInfo.setUserIntegral(userIntegral.toFloat())
            vipUserInfo.setConsumeTime(currentTime)
            vipUserInfo.setConsumeNumber(vipUserInfo.getConsumeNumber() + 1)
            vipUserDao.updateUserInfo(vipUserInfo)

            val dbUserConsumeInfo = DbUserConsumeInfo()
            dbUserConsumeInfo.setConsumeTime(GeneralUtils.getInstence().timeFormat(currentTime))
            dbUserConsumeInfo.setConsumeIntegral(thisJifen)
            dbUserConsumeInfo.setConsumeAmount(thisAmount)
            dbUserConsumeInfo.setContent("增加")
            dbUserConsumeInfo.setConverRatio("1元/" + jifenStr + "积分")
            dbUserConsumeInfo.setConsumeType(1)
            dbUserConsumeInfo.setUid(vipUserInfo.getUid())
            consumeUserDao.insertConsumeInfo(dbUserConsumeInfo)

            MyApplication.todayCount++
            MyApplication.todayConsume += thisAmount

            val userOperateRecordInfo = UserOperateRecordInfo()
            userOperateRecordInfo.uid = vipUserInfo.getUid()
            userOperateRecordInfo.userName = vipUserInfo.getUserName()
            userOperateRecordInfo.phoneNumber = vipUserInfo.getPhoneNumber()
            val content = "增加消费"+thisAmount.toString()
            userOperateRecordInfo.eventName = content
            userOperateRecordInfo.createTime = dbUserConsumeInfo.getConsumeTime()
            MyApplication.cacheOperateRecordList.add(0, userOperateRecordInfo)
            MyApplication.uploadBackupCount++

            listener.onSuccess()
            dismiss()
        } else {
            if (vipUserInfo.getCurrentAmount() - thisAmount >= 0) {

                val totalAmount =
                    CalculatorUtils.getInstence().subtract(vipUserInfo.getTotalAmount(), thisAmount)
                val currentAmount =
                    CalculatorUtils.getInstence().subtract(vipUserInfo.getCurrentAmount(), thisAmount)
                val totalIntegral =
                    CalculatorUtils.getInstence().subtract(vipUserInfo.getTotalIntegral(), thisJifen)
                val userIntegral =
                    CalculatorUtils.getInstence().subtract(vipUserInfo.getUserIntegral(), thisJifen)

                vipUserInfo.setExchangeNumber(thisAmount)
                vipUserInfo.setTotalAmount(totalAmount.toFloat())
                vipUserInfo.setCurrentAmount(currentAmount.toFloat())
                vipUserInfo.setTotalIntegral(totalIntegral.toFloat())
                vipUserInfo.setUserIntegral(userIntegral.toFloat())
                vipUserInfo.setConsumeTime(currentTime)
                vipUserDao.updateUserInfo(vipUserInfo)

                val dbUserConsumeInfo = DbUserConsumeInfo()
                dbUserConsumeInfo.setConsumeTime(GeneralUtils.getInstence().timeFormat(currentTime))
                dbUserConsumeInfo.setConsumeAmount(thisAmount)
                dbUserConsumeInfo.setConsumeIntegral(thisJifen)
                dbUserConsumeInfo.setContent("减去")
                dbUserConsumeInfo.setConsumeType(0)
                dbUserConsumeInfo.setConverRatio("1元/"+jifenStr+"积分")
                dbUserConsumeInfo.setUid(vipUserInfo.getUid())
                consumeUserDao.insertConsumeInfo(dbUserConsumeInfo)

                MyApplication.todayConsume -= thisAmount

                val userOperateRecordInfo = UserOperateRecordInfo()
                userOperateRecordInfo.uid = vipUserInfo.getUid()
                userOperateRecordInfo.userName = vipUserInfo.getUserName()
                userOperateRecordInfo.phoneNumber = vipUserInfo.getPhoneNumber()
                val content = "减去消费"+thisAmount.toString()
                userOperateRecordInfo.eventName = content
                userOperateRecordInfo.createTime = dbUserConsumeInfo.getConsumeTime()
                MyApplication.cacheOperateRecordList.add(0, userOperateRecordInfo)
                MyApplication.uploadBackupCount++

                listener.onSuccess()
                dismiss()
            } else {
                ToastUtils.show("减去金额不可大于当前金额")
            }
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_consume_confirm
    }
}