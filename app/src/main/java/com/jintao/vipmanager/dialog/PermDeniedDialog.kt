package com.jintao.vipmanager.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import com.jintao.vipmanager.R
import com.jintao.vipmanager.utils.GeneralUtils
import com.lxj.xpopup.interfaces.OnConfirmListener

/**
 * Author: jintao
 * CreateDate: 2022/4/15 14:57
 * Description:
 */
class PermDeniedDialog:Dialog {

    private var title:String
    private var desc:String
    private var listener:OnConfirmListener

    constructor(context: Context,title:String,desc:String,listener:OnConfirmListener): super(context, R.style.MyDialogStyle) {
        this.title = title
        this.desc = desc
        this.listener = listener
        val padding = GeneralUtils.getInstence().dip2px(context, 30f)
        val window: Window? = window
        val layoutParams: WindowManager.LayoutParams = window!!.getAttributes()
        layoutParams.gravity = Gravity.CENTER
        window?.getDecorView()?.setPadding(padding, 0, padding, 0)
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.dimAmount = 0.76f
        window?.setAttributes(layoutParams)
        setCancelable(false);
        setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_perm_denied)

        findViewById<TextView>(R.id.tv_title_dpd).setText(title)
        findViewById<TextView>(R.id.tv_desc_dpd).setText(desc)

        findViewById<TextView>(R.id.tv_confirm_dpd).setOnClickListener {
            listener.onConfirm()
            dismiss()
        }
    }
}