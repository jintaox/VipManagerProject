package com.jintao.vipmanager.viewmodel

import androidx.lifecycle.ViewModel
import com.jintao.vipmanager.database.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StatisticFromViewModel : ViewModel() {

    private var databaseRepository:DatabaseRepository
    private val monthArr = arrayOf("01","02","03","04","05","06","07","08","09","10","11","12")

    private val _loadConsumeResultStateFlow = MutableStateFlow<List<Float>>(mutableListOf())
    val loadConsumeResultStateFlow: StateFlow<List<Float>> = _loadConsumeResultStateFlow

    init {
        databaseRepository = DatabaseRepository()
    }

    suspend fun queryYearData(intYear: String) {
        val consumeList = mutableListOf<Float>()
        // 执行耗时操作，例如网络请求或数据库操作
        // 这里使用Dispatchers.IO来执行阻塞操作，不阻塞主线程
        for (index in 0 .. 11) {
            val qureyHead = intYear+"-"+monthArr[index]
            val queryYearMonthList = databaseRepository.queryByLikeDate(qureyHead)
            if (queryYearMonthList != null && queryYearMonthList.size != 0) {
                var monthConsumeNumber = 0f
                for (dbUserConsumeInfo in queryYearMonthList) {
                    if (dbUserConsumeInfo.getConsumeType() == 1) {//增加
                        monthConsumeNumber += dbUserConsumeInfo.getConsumeAmount()
                    }else {//减去
                        monthConsumeNumber -= dbUserConsumeInfo.getConsumeAmount()
                    }
                }
                consumeList.add(monthConsumeNumber)
            }else {
                consumeList.add(0f)
            }
        }
        // 使用data更新UI或其他操作
        _loadConsumeResultStateFlow.value = consumeList
    }
}