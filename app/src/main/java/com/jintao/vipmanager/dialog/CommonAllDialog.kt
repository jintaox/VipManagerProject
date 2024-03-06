package com.jintao.vipmanager.dialog

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.jintao.vipmanager.R
import com.jintao.vipmanager.databinding.DialogAllCommonBinding
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopup.interfaces.OnCancelListener
import com.lxj.xpopup.interfaces.OnConfirmListener

class CommonAllDialog(mContext: Context,var builder:Builder):CenterPopupView(mContext) {

    private lateinit var mBinding: DialogAllCommonBinding

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_all_common
    }

    override fun onCreate() {
        super.onCreate()
        mBinding = DialogAllCommonBinding.bind(contentView)
        if (builder.isShowTitle) {
            mBinding.tvTitle.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(builder.titleText)) {
                mBinding.tvTitle.setText(builder.titleText)
            }
        }else {
            mBinding.tvTitle.visibility = View.GONE
        }

        mBinding.clRootLayout.setOnClickListener {
            dismiss()
        }
        if (builder.isSingleButton) {
            mBinding.stvCancel.visibility = View.GONE
            mBinding.viewCenterLineNfwfew.visibility = View.GONE
        }else {
            mBinding.stvCancel.visibility = View.VISIBLE
            mBinding.viewCenterLineNfwfew.visibility = View.VISIBLE
        }
        if (!TextUtils.isEmpty(builder.descText)) {
            mBinding.tvDesc.setText(builder.descText)
        }
        mBinding.stvCancel.setOnClickListener {
            dismiss()
            builder.cancelListener?.onCancel()
        }
        mBinding.stvConfirm.setOnClickListener {
            if (builder.isClickConfirmDismiss) {
                dismiss()
            }
            builder.confirmListener?.onConfirm()
        }

        if (!TextUtils.isEmpty(builder.leftText)) {
            mBinding.stvCancel.setText(builder.leftText)
        }

        if (!TextUtils.isEmpty(builder.rightText)) {
            mBinding.stvConfirm.setText(builder.rightText)
        }
    }

    class Builder(var mContext: Context) {

        var isShowTitle = true  //title默认显示
        var isSingleButton = false  //默认双按钮显示
        var titleText = "Tips"    //默认title有内容
        var descText = ""
        var leftText = ""
        var rightText = ""
        var isClickConfirmDismiss = true
        var isBackKeyDismiss = false
        var isDismissTouchOutside = true
        var confirmListener:OnConfirmListener? = null
        var cancelListener:OnCancelListener? = null

        fun setClickConfirmDismiss(isClickConfirmDismiss:Boolean):Builder {
            this.isClickConfirmDismiss = isClickConfirmDismiss
            return this
        }

        fun setIsBackKeyDismiss(isBackKeyDismiss:Boolean):Builder {
            this.isBackKeyDismiss = !isBackKeyDismiss
            return this
        }

        fun setIsShowTitle(isShowTitle:Boolean):Builder {
            this.isShowTitle = isShowTitle
            return this
        }

        fun setIsSingleButton(isSingle:Boolean):Builder {
            this.isSingleButton = isSingle
            return this
        }

        fun setTitleContent(titleText:String):Builder {
            this.titleText = titleText
            return this
        }

        fun setDescContent(descText:String):Builder {
            this.descText = descText
            return this
        }

        fun setLeftButtonText(leftText:String):Builder {
            this.leftText = leftText
            return this
        }

        fun setRightButtonText(rightText:String):Builder {
            this.rightText = rightText
            return this
        }

        fun setOnConfirmListener(confirmListener: OnConfirmListener):Builder {
            this.confirmListener = confirmListener
            return this
        }
        fun setOnCancelListener(cancelListener: OnCancelListener):Builder {
            this.cancelListener = cancelListener
            return this
        }



        fun setOnTouchOutside(isDismiss: Boolean):Builder {
            this.isDismissTouchOutside = isDismiss
            return this
        }

        fun build() {
            XPopup.Builder(mContext)
                .autoFocusEditText(false)
                .isRequestFocus(false)
                .dismissOnTouchOutside(isDismissTouchOutside)
                .asCustom(CommonAllDialog(mContext,this))
                .show()
        }
    }
}