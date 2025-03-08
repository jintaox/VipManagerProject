package com.jintao.vipmanager.activity

import android.content.Intent
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.databinding.ActivityFunctionBinding
import com.jintao.vipmanager.utils.GeneralUtils

class FunctionActivity : BaseActivity<ActivityFunctionBinding>() {

    override fun initData() {
        mBinding.title.tvTitleContent.setText("全部功能")
    }

    override fun initListener() {

        mBinding.title.ivTitleBack.setOnClickListener {
            finish()
        }
        mBinding.sllStatisticFrom.setOnClickListener {
            if (GeneralUtils.getInstence().onClickEnable()) {
                val intent = Intent(this, StatisticFromActivity::class.java)
                startActivity(intent)
            }
        }
        mBinding.sllConvertGoodsTable.setOnClickListener {
            if (GeneralUtils.getInstence().onClickEnable()) {
                val intent = Intent(this, ConvertGoodsTableActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun initObserve() {

    }

    override fun getViewBinding(): ActivityFunctionBinding {
        return ActivityFunctionBinding.inflate(layoutInflater)
    }
}