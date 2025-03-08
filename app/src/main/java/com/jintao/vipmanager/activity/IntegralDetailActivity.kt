package com.jintao.vipmanager.activity

import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jintao.vipmanager.R
import com.jintao.vipmanager.adapter.IntegralDetailAdapter
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.database.DatabaseRepository
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo
import com.jintao.vipmanager.database.launchWithNotLoadingFlow
import com.jintao.vipmanager.databinding.ActivityIntegralDetailBinding
import com.jintao.vipmanager.listener.OnSelectPickerListener
import com.jintao.vipmanager.utils.GeneralUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopupext.listener.TimePickerListener
import com.lxj.xpopupext.popup.TimePickerPopup
import me.jingbin.library.ByRecyclerView
import java.util.*
import kotlin.collections.ArrayList

class IntegralDetailActivity : BaseActivity<ActivityIntegralDetailBinding>(), ByRecyclerView.OnLoadMoreListener {

    private lateinit var databaseRepository: DatabaseRepository
    private var consumeList:ArrayList<DbUserConsumeInfo> = arrayListOf()
    private lateinit var integralDetailAdapter:IntegralDetailAdapter
    private var vipUid = 0
    private var pageCount = 0
    private val pageSize = 20

    override fun initData() {
        mBinding.title.tvTitleContent.setText("会员消费明细")
        vipUid = intent.getIntExtra("vip_integral_uid", 0)
        val vipIntegral = intent.getFloatExtra("vip_integral_number", 0f)
        val vipAmount = intent.getFloatExtra("vip_amount_number", 0f)
        mBinding.tvUserIntegral.setText(" "+ GeneralUtils.getInstence().formatAmount(vipAmount)+"元   "+GeneralUtils.getInstence().formatAmount(vipIntegral)+"分")

        mBinding.rvIntegralList.setOnLoadMoreListener(this)
        mBinding.rvIntegralList.setRefreshEnabled(false)
        mBinding.rvIntegralList.setLoadMoreEnabled(false)

        val linearLayoutManager = LinearLayoutManager(this)
        mBinding.rvIntegralList.layoutManager = linearLayoutManager
        val dividerItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.home_divider_line_shape
            )!!
        )
        mBinding.rvIntegralList.addItemDecoration(dividerItemDecoration)

        databaseRepository = DatabaseRepository()
        launchWithNotLoadingFlow({ databaseRepository.queryByConsumeUidAll(vipUid,pageSize,0) }) {
            onSuccess = { dbList ->
                if (dbList.size==pageSize) {
                    mBinding.rvIntegralList.setLoadMoreEnabled(true)
                }
                consumeList.addAll(dbList)
                integralDetailAdapter = IntegralDetailAdapter(consumeList)
                mBinding.rvIntegralList.adapter = integralDetailAdapter
            }
        }
    }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener {
            finish()
        }
        mBinding.stvSelectDate.setOnClickListener {
            showTimePickerPopup(object :OnSelectPickerListener{
                override fun setResult(result: String) {
                    if (!TextUtils.isEmpty(result)) {
                        querySeleteDateConsumeList(vipUid,result)
                    }else {//恢复默认显示
                        integralDetailAdapter.setListData(consumeList)
                        integralDetailAdapter.notifyDataSetChanged()
                        if (mBinding.tvTipNull.visibility == View.VISIBLE) {
                            mBinding.tvTipNull.visibility = View.GONE
                        }
                    }
                }
            })
        }
    }

    private fun querySeleteDateConsumeList(vipUid: Int, result: String) {
        launchWithNotLoadingFlow({ databaseRepository.queryByConsumeUidDate(vipUid,result) }) {
            onSuccess = { dateList ->
                integralDetailAdapter.setListData(dateList)
                integralDetailAdapter.notifyDataSetChanged()
                if (dateList.size==0) {
                    if (mBinding.tvTipNull.visibility == View.GONE) {
                        mBinding.tvTipNull.visibility = View.VISIBLE
                    }
                }else {
                    if (mBinding.tvTipNull.visibility == View.VISIBLE) {
                        mBinding.tvTipNull.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun initObserve() {

    }

    private fun showTimePickerPopup(listener: OnSelectPickerListener) {
        val defaultDate = Calendar.getInstance()
        val currentYear = defaultDate.get(Calendar.YEAR)
        val startYear = currentYear - 5
        val popup =
            TimePickerPopup(this)
                .setMode(TimePickerPopup.Mode.YM)
                .setYearRange(startYear, currentYear)
                .setTimePickerListener(object : TimePickerListener {
                    override fun onTimeChanged(date: Date?) {

                    }

                    override fun onTimeConfirm(date: Date, view: View?) {
                        val selectYear = (date.year + 1900).toString()
                        var selectMonth = (date.month + 1).toString()
                        if (selectMonth.length == 1) {
                            selectMonth = "0" + selectMonth
                        }
                        val result = selectYear + "-" + selectMonth
                        listener.setResult(result)
                    }

                    override fun onCancel() {
                        listener.setResult("")
                    }
                })

        XPopup.Builder(this)
            .asCustom(popup)
            .show()
    }

    override fun getViewBinding(): ActivityIntegralDetailBinding {
        return ActivityIntegralDetailBinding.inflate(layoutInflater)
    }

    override fun onLoadMore() {
        pageCount++
        launchWithNotLoadingFlow({ databaseRepository.queryByConsumeUidAll(vipUid,pageSize,pageCount) }) {
            onSuccess = { dbList ->
                mBinding.rvIntegralList.loadMoreComplete()
                if (dbList.size < pageSize) {
                    mBinding.rvIntegralList.setLoadMoreEnabled(false)
                }
                consumeList.addAll(dbList)
                integralDetailAdapter.notifyDataSetChanged()
            }
        }
    }
}