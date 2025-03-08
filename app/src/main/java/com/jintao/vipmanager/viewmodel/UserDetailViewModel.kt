package com.jintao.vipmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jintao.vipmanager.database.DatabaseRepository
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Author: zhanghui
 * CreateDate: 2025/2/27 18:16
 * Description:
 */
class UserDetailViewModel : ViewModel() {

    private var databaseRepository: DatabaseRepository

    private val _loadUserFlowState = MutableStateFlow(DbVipUserInfo())
    val loadUserFlowState: StateFlow<DbVipUserInfo> = _loadUserFlowState

    init {
        databaseRepository = DatabaseRepository()
    }

    fun queryVipUserInfo(vipUserId: Long) {
        viewModelScope.launch {
            val vipUserInfo = databaseRepository.getVipUserDao().queryUserInfoFromId(vipUserId)
            _loadUserFlowState.value = vipUserInfo
        }
    }

    fun deleteUserInfo(vipUserInfo: DbVipUserInfo) {
        viewModelScope.launch {
            databaseRepository.getVipUserDao().deleteUserInfo(vipUserInfo)
        }
    }

    fun updateUserInfo(vipUserInfo: DbVipUserInfo) {
        viewModelScope.launch {
            databaseRepository.getVipUserDao().updateUserInfo(vipUserInfo)
        }
    }

    fun deleteUserConsumeData(uid: Int) {
        viewModelScope.launch {
            databaseRepository.getUserConsumeDao().deleteFormUid(uid)
        }
    }

    fun deleteUserConvertGoodsData(uid: Int) {
        viewModelScope.launch {
            databaseRepository.getConvertGoodsDao().deleteFormUid(uid)
        }
    }
}