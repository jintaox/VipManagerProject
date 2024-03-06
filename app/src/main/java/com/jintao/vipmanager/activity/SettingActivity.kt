package com.jintao.vipmanager.activity

import android.content.Intent
import android.text.TextUtils
import androidx.activity.result.contract.ActivityResultContracts
import com.hjq.http.EasyHttp
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.databinding.ActivitySettingBinding
import com.jintao.vipmanager.dialog.CommonAllDialog
import com.hjq.http.listener.OnDownloadListener
import android.text.Editable
import android.text.TextWatcher
import com.drhkj.pialn.listener.OnPictureUploadListener
import com.hjq.http.model.HttpMethod
import com.jintao.vipmanager.utils.*
import java.io.File
import java.lang.Exception
import java.text.DecimalFormat

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    private var isUploadDate = true
    private var isXiugaiJifen = false

    override fun initData() {
        mBinding.title.tvTitleContent.setText("个人信息")
        val loginInfo = LoginManager.getInstence().getLoginInfo()
        mBinding.tvUserName.setText(loginInfo.userName)
        mBinding.tvUserPhone.setText(loginInfo.phoneNumber)
        val vipNumber = intent.getLongExtra(AppConstant.VIP_USER_NUMBER, 0)
        mBinding.tvVipNumber.setText(vipNumber.toString())

        val saveJifen = MmkvUtil.getString(AppConstant.INTEGRAL_CONVERT_NUMBER, "1")
        mBinding.etInputJifen.setText(saveJifen)
    }

    private val mUserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode==200) {
                ToastUtils.show("名称已更改")
                val loginInfo = LoginManager.getInstence().getLoginInfo()
                mBinding.tvUserName.setText(loginInfo.userName)
            }
        }

    override fun initListener() {
        mBinding.title.ivTitleBack.setOnClickListener {
            finish()
        }
        mBinding.rlUserName.setOnClickListener {
            if (GeneralUtils.getInstence().onClickEnable()) {
                val intent = Intent(this, ChangeNameActivity::class.java)
                mUserLauncher.launch(intent)
            }
        }
        mBinding.ivRestoreDatabase.setOnClickListener {
            val inputFilename = mBinding.etInputFilename.editableText.toString()
            if (!TextUtils.isEmpty(inputFilename)) {
                CommonAllDialog.Builder(this)
                    .setTitleContent("恢复数据")
                    .setDescContent("恢复数据将会清空当前应用所有数据，确认恢复？")
                    .setOnConfirmListener{
                        restoreDatabase(inputFilename)
                    }
                    .build()
            }else {
                ToastUtils.show("请输入要恢复的文件名称")
            }
        }

        mBinding.etInputJifen.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                isXiugaiJifen = true
            }

        })
        mBinding.rlUploadDate.setOnClickListener{
            if (isUploadDate) {
                isUploadDate = false
                uploadDateFile()
            }
        }
        mBinding.rlChangePassword.setOnClickListener{
            startActivity(Intent(this,SetPasswordActivity::class.java))
        }
        mBinding.rlExitLogin.setOnClickListener {
            if (GeneralUtils.getInstence().onClickEnable()) {
                CommonAllDialog.Builder(this)
                    .setTitleContent("提示")
                    .setDescContent("是否退出登录？")
                    .setOnConfirmListener{
                        val loginInfo = LoginManager.getInstence().getLoginInfo()
                        loginInfo.userName = ""
                        loginInfo.isLogin = false
                        loginInfo.phoneNumber = ""
                        loginInfo.sign = ""
                        LoginManager.getInstence().saveLoginInfo(loginInfo)

                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    .build()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(isXiugaiJifen) {
            val inputJiFenStr = mBinding.etInputJifen.editableText.trim().toString()
            if (!TextUtils.isEmpty(inputJiFenStr)) {
                val saveJifen = MmkvUtil.getString(AppConstant.INTEGRAL_CONVERT_NUMBER, "1").toFloat()
                try {
                    val inputJiFen = inputJiFenStr.toFloat()
                    if(saveJifen != inputJiFen && inputJiFen > 0f) {
                        val decimalFormat = DecimalFormat("#.##")
                        val result = decimalFormat.format(inputJiFen)
                        MmkvUtil.putString(AppConstant.INTEGRAL_CONVERT_NUMBER, result)
                    }
                }catch (e:IllegalArgumentException) {
                    ToastUtils.show("数字格式错误")
                }
            }
        }
    }

    private fun restoreDatabase(inputFilename: String) {
        val loginInfo = LoginManager.getInstence().getLoginInfo()
        val downloadUrl = AppConfig.aliyunBaseUrl + loginInfo.phoneNumber +"/"+inputFilename + ".db"
        val sdcardDatabasePath = StreamUtils.getSdcardDatabasePath()
        val sdcardDatabaseFile = File(sdcardDatabasePath)
        if (sdcardDatabaseFile!=null&&sdcardDatabaseFile.exists()) {
            sdcardDatabaseFile.delete()
        }
        EasyHttp.download(this)
            .method(HttpMethod.GET)
            .file(sdcardDatabaseFile)
            .url(downloadUrl)
            .listener(object : OnDownloadListener {
                override fun onDownloadProgressChange(file: File?, progress: Int) {

                }

                override fun onDownloadSuccess(file: File?) {
                    val dataBasePath = getDatabasePath(AppConfig.DATABASE_FILE_NAME).path
                    File(dataBasePath).delete()
                    System.exit(0)
                }

                override fun onDownloadFail(file: File?, e: Exception?) {
                    ToastUtils.show("恢复文件不存在，请联系管理员")
                }
            }).start()
    }

    private fun uploadDateFile() {
        val dataBasePath = getDatabasePath(AppConfig.DATABASE_FILE_NAME).path
        val dataBaseFile = File(dataBasePath)
        if (dataBaseFile!=null&&dataBaseFile.exists()) {
            showLoadingDialog("正在上传...")
            UploadHelper.uploadAliyunFile(dataBasePath,object : OnPictureUploadListener {
                override fun onResultSuccess(path: String) {
                    runOnUiThread {
                        dismissLoadingDialog()
                        ToastUtils.show("上传成功")
                    }
                }

                override fun onResultFail() {
                    runOnUiThread {
                        dismissLoadingDialog()
                        isUploadDate = true
                        ToastUtils.show("上传失败")
                    }
                }
            })
        }else {
            isUploadDate = true
            ToastUtils.show("数据文件异常，无法上传")
        }
    }

    override fun getViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }
}