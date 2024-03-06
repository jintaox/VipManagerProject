package com.jintao.vipmanager.activity

import android.view.KeyEvent
import com.drhkj.pialn.listener.OnPictureUploadListener
import com.jintao.vipmanager.R
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.databinding.ActivityHomeBinding
import com.jintao.vipmanager.fragment.HomeFragment
import com.jintao.vipmanager.utils.*
import java.io.File

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    override fun setStatusBarColor() = "#13BEFF"
    override fun setStatusBarDarkFont() = false
    private val CACHE_INTERVAL_TIME = 1000 * 60 * 10  //10分钟

    override fun initData() {
        //这种操作是防止app卸载，数据丢失，如果app被卸载，重装app，数据自动恢复正常
        val dataBaseFile = File(getDatabasePath(AppConfig.DATABASE_FILE_NAME).path)
        if (dataBaseFile !=null && dataBaseFile.exists()) {
            //如果app内部数据库文件存在，正常使用
            showHomeFragment()
        }else {//内部数据库文件不存在，判断是否需要恢复数据
            //如果sd卡存在缓存的数据库文件，则开始恢复数据
            val sdcardDatabaseFile = File(StreamUtils.getSdcardDatabasePath())
            if (sdcardDatabaseFile != null && sdcardDatabaseFile.exists()) {
                //新安装app检查存在原数据库，直接恢复使用
                val todayDateStr = GeneralUtils.getInstence().getTodayDateStr()
                MmkvUtil.putString(AppConstant.FILE_UPLOAD_TIME, todayDateStr)
                showLoadingDialog("正在初始化...")
                Thread {//将sd卡缓存的备用数据库文件，恢复到app的databases目录下
                    StreamUtils.copyFileStream(sdcardDatabaseFile, dataBaseFile)
                    runOnUiThread {
                        dismissLoadingDialog()
                        showHomeFragment()
                    }
                }.start()
            } else {//不存在缓存数据库，直接初始化
                showHomeFragment()
            }
        }
    }

    private fun showHomeFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_fl_container, HomeFragment(), "homefragment")
        transaction.commit()
    }

    override fun onResume() {
        super.onResume()

        val todayDateStr = GeneralUtils.getInstence().getTodayDateStr()
        val fileUploadTime = MmkvUtil.getString(AppConstant.FILE_UPLOAD_TIME, "")
        //判断当天是否上传过数据库文件，没有上传，则自动上传备份，防止设备损坏等不可抗力因素数据丢失
        if (!fileUploadTime.equals(todayDateStr)) {
            val dataBasePath = getDatabasePath(AppConfig.DATABASE_FILE_NAME).path
            val dataBaseFile = File(dataBasePath)
            if (dataBaseFile != null && dataBaseFile.exists()) {
                UploadHelper.uploadAliyunFile(dataBasePath, object : OnPictureUploadListener {
                    override fun onResultSuccess(path: String) {
                        MmkvUtil.putString(AppConstant.FILE_UPLOAD_TIME, todayDateStr)
                    }

                    override fun onResultFail() {
                    }
                })
            }
        }
    }

    override fun onDestroy() {
        val saveTime = MmkvUtil.getLong(AppConstant.LOCALE_SAVE_TIME, 0L)
        val currentTime = System.currentTimeMillis()
        //每次退出app，则自动缓存本地数据库，设置缓存时间间隔，防止频繁打开关闭app一直IO
        if (currentTime - saveTime > CACHE_INTERVAL_TIME) {
            MmkvUtil.putLong(AppConstant.LOCALE_SAVE_TIME, currentTime)
            val dataBaseFile = File(getDatabasePath(AppConfig.DATABASE_FILE_NAME).path)
            val sdcardDatabasePath = StreamUtils.getSdcardDatabasePath()
            val sdcardDatabaseFile = File(sdcardDatabasePath)
            if (sdcardDatabaseFile != null && sdcardDatabaseFile.exists()) {
                sdcardDatabaseFile.delete()
            }
            Thread {
                StreamUtils.copyFileStream(dataBaseFile, sdcardDatabaseFile)
            }.start()
        }
        super.onDestroy()
    }

    override fun initListener() {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val findFragment = supportFragmentManager.findFragmentByTag("homefragment")
            if (findFragment != null) {
                val backPressed = (findFragment as HomeFragment).setBackPressed()
                if (backPressed) {
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun getViewBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

}