package com.jintao.vipmanager.viewmodel

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.drhkj.pialn.listener.OnPictureUploadListener
import com.hjq.gson.factory.GsonFactory
import com.jintao.vipmanager.MyApplication
import com.jintao.vipmanager.database.DatabaseRepository
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.utils.AppConfig
import com.jintao.vipmanager.utils.AppConstant
import com.jintao.vipmanager.utils.GeneralUtils
import com.jintao.vipmanager.utils.MmkvUtil
import com.jintao.vipmanager.utils.RandomNameUtils
import com.jintao.vipmanager.utils.StreamUtils
import com.jintao.vipmanager.utils.UploadHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * Author: zhanghui
 * CreateDate: 2025/1/24 15:52
 * Description:
 */
class HomeViewModel : ViewModel() {

    private val CACHE_INTERVAL_TIME = 1000 * 60 * 30
    private var databaseRepository: DatabaseRepository

    private val _loadDatabaseState = MutableStateFlow<Boolean>(false)
    val loadDatabaseState: StateFlow<Boolean> = _loadDatabaseState

    init {
        databaseRepository = DatabaseRepository()
    }

    fun initDataBaseFile(mActivity: Activity) {
        val dataBaseFile = File(mActivity.getDatabasePath(AppConfig.DATABASE_FILE_NAME).path)
        if (dataBaseFile != null && dataBaseFile.exists()) {
            _loadDatabaseState.value = true
        } else {//内部数据库文件不存在，判断是否需要恢复数据
            //如果sd卡存在缓存的数据库文件，则开始恢复数据
            val sdcardDatabaseFile = File(StreamUtils.getSdcardDatabasePath())
            if (sdcardDatabaseFile != null && sdcardDatabaseFile.exists()) {
                //新安装app检查存在原数据库，直接恢复使用
                val todayDateStr = GeneralUtils.getInstence().getTodayDateStr()
                MmkvUtil.putString(AppConstant.FILE_UPLOAD_TIME, todayDateStr)
                viewModelScope.launch(Dispatchers.IO) {
                    // 执行耗时操作，例如网络请求或数据库操作
                    // 这里使用Dispatchers.IO来执行阻塞操作，不阻塞主线程
                    StreamUtils.copyFileStream(sdcardDatabaseFile, dataBaseFile)
                    // 使用data更新UI或其他操作
                    _loadDatabaseState.value = true
                }
            } else {//不存在缓存数据库，直接初始化
                _loadDatabaseState.value = true
            }
        }
    }

    fun checkUploadDataBase(mActivity: Activity) {
        val todayDateStr = GeneralUtils.getInstence().getTodayDateStr()
        val fileUploadTime = MmkvUtil.getString(AppConstant.FILE_UPLOAD_TIME, "")
        if (!fileUploadTime.equals(todayDateStr)) {
            val dataBasePath = mActivity.getDatabasePath(AppConfig.DATABASE_FILE_NAME).path
            val dataBaseFile = File(dataBasePath)
            if (dataBaseFile != null && dataBaseFile.exists()) {
                UploadHelper.uploadImage(dataBasePath, object : OnPictureUploadListener {
                    override fun onResultSuccess(path: String) {
                        MmkvUtil.putString(AppConstant.FILE_UPLOAD_TIME, todayDateStr)
                    }

                    override fun onResultFail() {
                    }
                })
            }
        }
    }

    fun checkSaveDatabaseToSdcard(mActivity: Activity) {
        val saveTime = MmkvUtil.getLong(AppConstant.LOCALE_SAVE_TIME, 0L)
        val currentTime = System.currentTimeMillis()
        if (currentTime - saveTime > CACHE_INTERVAL_TIME) {
            MmkvUtil.putLong(AppConstant.LOCALE_SAVE_TIME, currentTime)
            val dataBaseFile = File(mActivity.getDatabasePath(AppConfig.DATABASE_FILE_NAME).path)
            val sdcardDatabasePath = StreamUtils.getSdcardDatabasePath()
            val sdcardDatabaseFile = File(sdcardDatabasePath)
            if (sdcardDatabaseFile != null && sdcardDatabaseFile.exists()) {
                sdcardDatabaseFile.delete()
            }
            GlobalScope.launch {
                StreamUtils.copyFileStream(dataBaseFile, sdcardDatabaseFile)
            }
        }
    }

    fun uploadBackupFile(mActivity: Activity) {
        val backupFilePath = StreamUtils.getBackupFilePath(mActivity)
        if (!TextUtils.isEmpty(backupFilePath)) {
            val recordJson =
                GsonFactory.getSingletonGson().toJson(MyApplication.cacheOperateRecordList)
            StreamUtils.writeFile(backupFilePath, recordJson)
            UploadHelper.uploadFile(backupFilePath, AppConfig.BACKUP_DATA_NAME, object :
                OnPictureUploadListener {
                override fun onResultSuccess(path: String) {

                }

                override fun onResultFail() {

                }
            })
        }
    }

    fun updateUserInfo(userInfo: DbVipUserInfo) {
        viewModelScope.launch {
            databaseRepository.getVipUserDao().updateUserInfo(userInfo)
        }
    }

    fun deleteConsumeInfo(userConsumeInfo: DbUserConsumeInfo) {
        viewModelScope.launch {
            databaseRepository.getUserConsumeDao().deleteConsumeInfo(userConsumeInfo)
        }
    }

    suspend fun testBatchAddUsers(size: Int) : Long {
        val startTime = System.currentTimeMillis()
        val phoneList = StreamUtils.generatePhoneNum(size)
        val nameList = RandomNameUtils.getNameList(size)
        val mutableListOf = mutableListOf<DbVipUserInfo>()
        for (index in 0 until size) {
            val phoneInfo = phoneList.get(index)
            val isPhoneAvai = databaseRepository.queryByVipPhoneAvai(phoneInfo)
            if (isPhoneAvai) {
                val generateUid = databaseRepository.getNewVipUid(phoneInfo)
                val currentTime = System.currentTimeMillis()
                val dbVipUserInfo = DbVipUserInfo()
                dbVipUserInfo.setUserName(nameList.get(index))
                dbVipUserInfo.setPhoneNumber(phoneInfo)
                dbVipUserInfo.setUid(generateUid)

                dbVipUserInfo.setExchangeNumber(0f)
                dbVipUserInfo.setTotalAmount(0f)
                dbVipUserInfo.setCurrentAmount(0f)
                dbVipUserInfo.setUserIntegral(0f)
                dbVipUserInfo.setTotalIntegral(0f)
                dbVipUserInfo.setConsumeTime(currentTime)
                dbVipUserInfo.setRegisterTime(GeneralUtils.getInstence().timeFormat(currentTime))
                dbVipUserInfo.setConsumeNumber(0)
                mutableListOf.add(dbVipUserInfo)
            }
        }
        databaseRepository.getVipUserDao().insertUserList(mutableListOf)
        val stopTime = System.currentTimeMillis() - startTime
        Log.e("AAAAAA", "耗时：" + stopTime.toString())
        return stopTime
    }
}