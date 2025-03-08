package com.jintao.vipmanager.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class GeneralUtils{

    companion object {
        private var mInstance : GeneralUtils? = null
        @Synchronized
        fun getInstence(): GeneralUtils {
            if (mInstance ==null) {
                mInstance = GeneralUtils()
            }
            return mInstance!!
        }
    }


    private var startClickTime: Long = 0

    fun onClickEnable(): Boolean {
        var isFlag = false
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - startClickTime > 500) {
            startClickTime = currentTimeMillis
            isFlag = true
        }
        return isFlag
    }


    fun dip2px(context: Context, dpValue: Float): Int {
        val getdede = context.resources.displayMetrics.density
        return (dpValue * getdede + 0.5f).toInt()
    }

    fun formatAmount(fnum: Float): String {
        val decimalFormat = DecimalFormat("###,###.##")
        return decimalFormat.format(fnum)
    }

    fun networkAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo?.isAvailable ?: false
    }

    fun setRichText(originText: String, richText: String, textView: TextView) {
        val startIndex = originText.lastIndexOf(richText)
        if (startIndex!=-1) {
            val spannableBuilder = SpannableStringBuilder(originText)
            val stopIndex = startIndex + richText.length
            spannableBuilder.setSpan(
                ForegroundColorSpan(Color.parseColor("#FF6577")),
                startIndex,
                stopIndex,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            textView.text = spannableBuilder
        }else {
            textView.text = originText
        }
    }

    fun isNumberic(content: String): Boolean {
        if (content.toDoubleOrNull()!=null) {
            return true
        }else {
            return false
        }
    }
    /**
     * 进入Android 11或更高版本的文件访问权限页面
     */
    fun goManagerFileAccess(activity: Activity) {
        // Android 11 (Api 30)或更高版本的写文件权限需要特殊申请，需要动态申请管理所有文件的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val appIntent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            appIntent.data = Uri.parse("package:${activity.packageName}")
            //appIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
            try {
                activity.startActivity(appIntent)
            } catch (ex: ActivityNotFoundException) {
                ex.printStackTrace()
                val allFileIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activity.startActivity(allFileIntent)
            }
        }
    }

    fun isSupport64Cpu():Boolean {
        val supportedAbis = Build.SUPPORTED_ABIS
        var isSupport64 = false
        for (abi in supportedAbis) {
            if (abi.equals("arm64-v8a")) {
                isSupport64 = true
            }
        }
        return isSupport64
    }

    fun timeFormat(timestamp:Long):String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val dateString = formatter.format(date)
        return dateString
    }

    fun dataToTimestamp(dataStr:String):Long {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val date = formatter.parse(dataStr)
        val timestamp = date.time
        return timestamp
    }

    fun getTodayDateStr():String {
        val currentTimeMillis = System.currentTimeMillis()
        val date = Date(currentTimeMillis)
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val dateString = formatter.format(date)
        return dateString
    }


    fun generateRandomUid(inputPhone: String): String {
        val phoneTail = inputPhone.substring(inputPhone.length - 4)
        val sb = StringBuffer()
        val random = Random()
        var firsthead = random.nextInt(9)+1
        sb.append(firsthead)
        sb.append(phoneTail)
        for (i in 0..3) {
            sb.append(random.nextInt(10))
        }
        return sb.toString()
    }
}