package com.jintao.vipmanager.dialog

import android.content.Context
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.MyApplication
import com.jintao.vipmanager.R
import com.jintao.vipmanager.bean.UserOperateRecordInfo
import com.jintao.vipmanager.database.DatabaseRepository
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo
import com.jintao.vipmanager.databinding.DialogIntegralChangeBinding
import com.jintao.vipmanager.listener.OnIntegralChangeListener
import com.jintao.vipmanager.utils.AppConstant
import com.jintao.vipmanager.utils.CalculatorUtils
import com.jintao.vipmanager.utils.GeneralUtils
import com.jintao.vipmanager.utils.MmkvUtil
import com.lxj.xpopup.impl.FullScreenPopupView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

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
                    launchFun { saveUserAmountInfo(2,inputAmount.toFloat()) }
                    lifecycleScope.launch {  }
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
                        launchFun { saveUserAmountInfo(1,inputAmount.toFloat()) }
                    }else {
                        val multipleAmount = addMultipleAmount(amountArr)
                        if (multipleAmount >= 0f) {
                            launchFun { saveUserAmountInfo(1,multipleAmount) }
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

    private suspend fun saveUserAmountInfo(type:Int, thisAmount: Float) {
        val saveBigAmount = MmkvUtil.getString(AppConstant.BIG_AMOUNT_TIP, "500").toFloat()
        if (thisAmount > saveBigAmount) {
            listener.onRepeatConfirm(type,thisAmount)
            dismiss()
            return
        }
        val databaseRepository = DatabaseRepository()
        val vipUserDao = databaseRepository.getVipUserDao()
        val vipUserInfo = vipUserDao.queryUserInfoFromId(vipUserId)

        val jifenStr = MmkvUtil.getString(AppConstant.INTEGRAL_CONVERT_NUMBER, "1")
        val thisJifen = thisAmount * jifenStr.toFloat()
        val consumeUserDao = databaseRepository.getUserConsumeDao()
        val currentTime = System.currentTimeMillis()
        if (type==1) {
            val totalAmount = CalculatorUtils.getInstence().add(vipUserInfo.getTotalAmount(),thisAmount)
            val currentAmount = CalculatorUtils.getInstence().add(vipUserInfo.getCurrentAmount(),thisAmount)
            val totalIntegral = CalculatorUtils.getInstence().add(vipUserInfo.getTotalIntegral(),thisJifen)
            val userIntegral = CalculatorUtils.getInstence().add(vipUserInfo.getUserIntegral(),thisJifen)

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

            MyApplication.todayCount ++
            MyApplication.todayConsume += thisAmount

            if (thisAmount > 15) {//大于15才记录，金额太小直接忽略
                val userOperateRecordInfo = UserOperateRecordInfo()
                userOperateRecordInfo.uid = vipUserInfo.getUid()
                userOperateRecordInfo.userName = vipUserInfo.getUserName()
                userOperateRecordInfo.phoneNumber = vipUserInfo.getPhoneNumber()
                val content = "增加消费"+thisAmount.toString()
                userOperateRecordInfo.eventName = content
                userOperateRecordInfo.createTime = dbUserConsumeInfo.getConsumeTime()
                MyApplication.cacheOperateRecordList.add(0, userOperateRecordInfo)
                MyApplication.uploadBackupCount++
            }
            listener.onSuccess()
            dismiss()
        }else {
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

                if (thisAmount > 15) {//大于15才记录，金额太小直接忽略
                    val userOperateRecordInfo = UserOperateRecordInfo()
                    userOperateRecordInfo.uid = vipUserInfo.getUid()
                    userOperateRecordInfo.userName = vipUserInfo.getUserName()
                    userOperateRecordInfo.phoneNumber = vipUserInfo.getPhoneNumber()
                    val content = "减去消费"+thisAmount.toString()
                    userOperateRecordInfo.eventName = content
                    userOperateRecordInfo.createTime = dbUserConsumeInfo.getConsumeTime()
                    MyApplication.cacheOperateRecordList.add(0, userOperateRecordInfo)
                    MyApplication.uploadBackupCount++
                }
                listener.onSuccess()
                dismiss()
            }else {
                ToastUtils.show("减去金额不可大于当前金额")
            }
        }
    }

    private fun launchFun(requestBlock: suspend () -> Unit) {
        lifecycleScope.launch {
            flow {
                emit(requestBlock())
            }.collect()
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_integral_change
    }
}