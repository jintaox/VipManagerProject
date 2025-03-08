package com.jintao.vipmanager.dialog

import android.content.Context
import android.text.TextUtils
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.R
import com.jintao.vipmanager.databinding.DialogCheXiaoBinding
import com.jintao.vipmanager.utils.AppConstant
import com.jintao.vipmanager.utils.MmkvUtil
import com.lxj.xpopup.impl.FullScreenPopupView
import com.lxj.xpopup.interfaces.OnConfirmListener

/**
 * Author: jintao
 * CreateDate: 2024/1/30 21:24
 * Description:
 */
class CheXiaoDialog(mContext:Context, var title:String,var desc:String, var listener: OnConfirmListener): FullScreenPopupView(mContext) {

    private lateinit var mBinding:DialogCheXiaoBinding

    override fun onCreate() {
        super.onCreate()
        mBinding = DialogCheXiaoBinding.bind(contentView)
        mBinding.stvCancel.setOnClickListener { dismiss() }
        mBinding.clRootLayout.setOnClickListener { dismiss() }

        mBinding.tvTitle.setText(title)
        mBinding.tvDesc.setText(desc)

        val savePwd = MmkvUtil.getString(AppConstant.ADMIN_PASSWORD_ID, "2580")

        mBinding.stvConfirm.setOnClickListener {
            val inputPassword = mBinding.etInputPassword.editableText.toString()
            if (!TextUtils.isEmpty(inputPassword)) {
                if (inputPassword.equals(savePwd)) {
                    listener.onConfirm()
                    dismiss()
                }else {
                    ToastUtils.show("密码错误")
                    dismiss()
                }
            }else {
                ToastUtils.show("请输入管理员密码")
            }
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_che_xiao
    }
}