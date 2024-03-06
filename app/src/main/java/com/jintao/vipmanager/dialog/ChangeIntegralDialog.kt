package com.jintao.vipmanager.dialog

import android.content.Context
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.MyApplication
import com.jintao.vipmanager.R
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.helper.DbVipUserHelper
import com.jintao.vipmanager.databinding.DialogIntegralChangeBinding
import com.jintao.vipmanager.listener.OnIntegralChangeListener
import com.jintao.vipmanager.utils.AppConstant
import com.jintao.vipmanager.utils.CalculatorUtils
import com.jintao.vipmanager.utils.GeneralUtils
import com.jintao.vipmanager.utils.MmkvUtil
import com.lxj.xpopup.impl.FullScreenPopupView

/**
 * Author: jintao
 * CreateDate: 2023/8/26 13:12
 * Description:
 */
class ChangeIntegralDialog(var mContext: Context, var title:String,var vipUserId: Long,var listener: OnIntegralChangeListener):FullScreenPopupView(mContext) {

    override fun onCreate() {
        super.onCreate()
        val mBinding = DialogIntegralChangeBinding.bind(contentView)
        mBinding.tvTitle.setText(title)
        mBinding.stvCancel.setOnClickListener {
            dismiss()
        }
        mBinding.stvReduceNumber.setOnClickListener {
            val inputAmount = mBinding.etInputAmount.editableText.toString()
            if (!TextUtils.isEmpty(inputAmount)) {
                try {
                    saveUserAmountInfo(2,inputAmount.toFloat())
                }catch (e:NumberFormatException) {
                    ToastUtils.show("请输入正确的金额")
                }
            }else {
                ToastUtils.show("请输入金额")
            }
        }
        mBinding.stvAddNumber.setOnClickListener {
            val inputAmount = mBinding.etInputAmount.editableText.toString()
            if (!TextUtils.isEmpty(inputAmount)) {
                try {
                    val amountArr = inputAmount.split("+")
                    if (amountArr.size==1) {
                        saveUserAmountInfo(1,inputAmount.toFloat())
                    }else {
                        val multipleAmount = addMultipleAmount(amountArr)
                        if (multipleAmount >= 0f) {
                            saveUserAmountInfo(1,multipleAmount)
                        }else {
                            ToastUtils.show("请输入正确的金额")
                        }
                    }
                }catch (e:NumberFormatException) {
                    ToastUtils.show("请输入正确的金额")
                }
            }else {
                ToastUtils.show("请输入金额")
            }
        }
        mBinding.viewKongbai1.setOnClickListener { dismiss() }
        mBinding.viewKongbai2.setOnClickListener { dismiss() }
        mBinding.etInputAmount.setFocusable(true)
        mBinding.etInputAmount.setFocusableInTouchMode(true);
        mBinding.etInputAmount.requestFocus()
        val inputManager= mBinding.etInputAmount.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(mBinding.etInputAmount, 0)
    }

    private fun addMultipleAmount(amountArr: List<String>):Float {
        var result = 0f
        for (index in 0 until amountArr.size) {
            try {
                val itemAmount = amountArr.get(index).toFloat()
                result += itemAmount
            }catch (e:NumberFormatException) {
                return -1f
            }
        }
        return result
    }

    private fun saveUserAmountInfo(type:Int, thisAmount: Float) {
        val vipUserDao = DbVipUserHelper(mContext, DbVipUserInfo::class.java).vipUserDao
        val vipUserInfo = vipUserDao.queryById(vipUserId)

        val jifenStr = MmkvUtil.getString(AppConstant.INTEGRAL_CONVERT_NUMBER, "1")
        val thisJifen = thisAmount * jifenStr.toFloat()
        val consumeUserDao = DbVipUserHelper(mContext, DbUserConsumeInfo::class.java).userConsumeDao
        val currentTime = System.currentTimeMillis()
        if (type==1) {
            val totalAmount = CalculatorUtils.getInstence().add(vipUserInfo.totalAmount,thisAmount)
            val currentAmount = CalculatorUtils.getInstence().add(vipUserInfo.currentAmount,thisAmount)
            val totalIntegral = CalculatorUtils.getInstence().add(vipUserInfo.totalIntegral,thisJifen)
            val userIntegral = CalculatorUtils.getInstence().add(vipUserInfo.userIntegral,thisJifen)

            vipUserInfo.lastAmount = thisAmount
            vipUserInfo.totalAmount = totalAmount.toFloat()
            vipUserInfo.currentAmount = currentAmount.toFloat()
            vipUserInfo.totalIntegral = totalIntegral.toFloat()
            vipUserInfo.userIntegral = userIntegral.toFloat()
            vipUserInfo.consumeTime = currentTime
            vipUserInfo.consumeNumber = vipUserInfo.consumeNumber+1
            vipUserDao.update(vipUserInfo)

            val dbUserConsumeInfo = DbUserConsumeInfo()
            dbUserConsumeInfo.consumeTime  =  GeneralUtils.getInstence().timeFormat(currentTime)
            dbUserConsumeInfo.consumeIntegral = thisJifen
            dbUserConsumeInfo.consumeAmount = thisAmount
            dbUserConsumeInfo.content = "增加"
            dbUserConsumeInfo.converRatio = "1元/"+jifenStr+"积分"
            dbUserConsumeInfo.consumeType = true
            dbUserConsumeInfo.uid = vipUserInfo.uid
            consumeUserDao.insert(dbUserConsumeInfo)

            MyApplication.todayCount ++
            MyApplication.todayConsume += thisAmount

            listener.onSuccess()
            dismiss()
        }else {
            if (vipUserInfo.currentAmount - thisAmount >= 0) {

                val totalAmount = CalculatorUtils.getInstence().subtract(vipUserInfo.totalAmount,thisAmount)
                val currentAmount = CalculatorUtils.getInstence().subtract(vipUserInfo.currentAmount,thisAmount)
                val totalIntegral = CalculatorUtils.getInstence().subtract(vipUserInfo.totalIntegral,thisJifen)
                val userIntegral = CalculatorUtils.getInstence().subtract(vipUserInfo.userIntegral,thisJifen)

                vipUserInfo.lastAmount = thisAmount
                vipUserInfo.totalAmount = totalAmount.toFloat()
                vipUserInfo.currentAmount = currentAmount.toFloat()
                vipUserInfo.totalIntegral = totalIntegral.toFloat()
                vipUserInfo.userIntegral = userIntegral.toFloat()
                vipUserInfo.consumeTime = currentTime
                vipUserDao.update(vipUserInfo)

                val dbUserConsumeInfo = DbUserConsumeInfo()
                dbUserConsumeInfo.consumeTime  =  GeneralUtils.getInstence().timeFormat(currentTime)
                dbUserConsumeInfo.consumeAmount = thisAmount
                dbUserConsumeInfo.consumeIntegral = thisJifen
                dbUserConsumeInfo.content = "减去"
                dbUserConsumeInfo.consumeType = false
                dbUserConsumeInfo.converRatio = "1元/"+jifenStr+"积分"
                dbUserConsumeInfo.uid = vipUserInfo.uid
                consumeUserDao.insert(dbUserConsumeInfo)

                MyApplication.todayConsume -= thisAmount

                listener.onSuccess()
                dismiss()
            }else {
                ToastUtils.show("减去金额不可大于当前金额")
            }
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_integral_change
    }
}