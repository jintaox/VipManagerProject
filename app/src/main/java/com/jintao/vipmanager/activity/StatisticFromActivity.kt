package com.jintao.vipmanager.activity

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.database.collectIn
import com.jintao.vipmanager.database.launchWithFlowAndLoading
import com.jintao.vipmanager.databinding.ActivityStatisticFromBinding
import com.jintao.vipmanager.myview.MySpinnerView
import com.jintao.vipmanager.viewmodel.StatisticFromViewModel
import com.jintao.vipmanager.utils.GeneralUtils
import java.util.Calendar

class StatisticFromActivity : BaseActivity<ActivityStatisticFromBinding>() {

    private val yearList = mutableListOf<String>()
    private val monthList = mutableListOf<String>()
    private val tableIndexArray = arrayOf(0f,1f,2f,3f,4f,5f,6f,7f,8f,9f,10f,11f)
    private val barDataList = arrayListOf<BarEntry>()
    private lateinit var mViewModel: StatisticFromViewModel

    override fun initObserve() {
        mViewModel = ViewModelProvider(this).get(StatisticFromViewModel::class.java)

        mViewModel.loadConsumeResultStateFlow.collectIn(this, Lifecycle.State.CREATED) {
            onSuccess = { consumeList ->
                if (consumeList.size != 0) {
                    barDataList.clear()
                    var totalYearAmount = 0f
                    for (index in 0..11) {
                        val tableIndex = tableIndexArray[index]
                        val monthTotalConsume = consumeList[index]
                        barDataList.add(BarEntry(tableIndex, monthTotalConsume))
                        totalYearAmount += monthTotalConsume
                    }
                    mBinding.tvYearTotalData.setText(
                        GeneralUtils.getInstence().formatAmount(totalYearAmount) + "元"
                    )
                    initChart()
                }
            }
        }
    }

    override fun initData() {
        mBinding.title.tvTitleContent.setText("营收统计")

        yearList.clear()
        val calendar: Calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        for (indexYear in currentYear downTo 2023) {
            yearList.add(indexYear.toString()+"年")
        }

        mBinding.spinnerYearList.setItems(yearList)
        mBinding.spinnerYearList.setSelectedIndex(0)
        initMonthList()

        val intYear = getIntYear(yearList[0])
        launchWithFlowAndLoading{ mViewModel.queryYearData(intYear) }
    }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener { finish() }

        mBinding.spinnerYearList.setOnItemSelectedListener(MySpinnerView.OnItemSelectedListener { oldPostion, position, item ->
            if (position != -1) {
                showLoadingDialog()
                val intYear = getIntYear(yearList[position])
                launchWithFlowAndLoading{ mViewModel.queryYearData(intYear) }
            }
        })
    }

    private fun initChart() {
        val barDataSet = BarDataSet(barDataList, "营收统计")
        barDataSet.color = -0xff5d30
        val barData = BarData(barDataSet)
        barData.barWidth = 0.8f
        mBinding.barChart.data = barData
        mBinding.barChart.description.isEnabled = false
        mBinding.barChart.setFitBars(true)
        mBinding.barChart.setScaleEnabled(false)
        mBinding.barChart.setTouchEnabled(false)

        val barChartXAxis = mBinding.barChart.xAxis
        barChartXAxis.setDrawGridLines(false)
        barChartXAxis.valueFormatter = IndexAxisValueFormatter(monthList)
        barChartXAxis.granularity = 1.0f
        barChartXAxis.isGranularityEnabled = true
        barChartXAxis.position = XAxis.XAxisPosition.BOTTOM
        barChartXAxis.setDrawAxisLine(false)
        mBinding.barChart.legend.isEnabled = false
        mBinding.barChart.axisRight.isEnabled = false
        mBinding.barChart.axisLeft.setStartAtZero(true)

        // 刷新图表
        mBinding.barChart.invalidate()
    }

    private fun getIntYear(yearStr:String): String {
        val selectYearStr = yearStr.replace("年", "")
        return selectYearStr
    }

    private fun initMonthList() {
        monthList.add("1月")
        monthList.add("2月")
        monthList.add("3月")
        monthList.add("4月")
        monthList.add("5月")
        monthList.add("6月")
        monthList.add("7月")
        monthList.add("8月")
        monthList.add("9月")
        monthList.add("10月")
        monthList.add("11月")
        monthList.add("12月")
    }

    override fun getViewBinding(): ActivityStatisticFromBinding {
        return ActivityStatisticFromBinding.inflate(layoutInflater)
    }

}