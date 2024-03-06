package com.jintao.token

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class EncryptUtil private constructor(){

    companion object {
        private var mInstance: EncryptUtil? = null

        fun getInstance(): EncryptUtil {
            synchronized(EncryptUtil::class.java) {
                if (mInstance == null) {
                    mInstance = EncryptUtil()
                }
                return mInstance!!
            }
        }
    }

    fun timeToToken(serial_number:Int):Array<String> {
        val currentTimeMillis = System.currentTimeMillis()
        val date = Date(currentTimeMillis)
        val formatter = SimpleDateFormat("yyyy-MM-dd-HH")
        val dateString = formatter.format(date)

        val dateArr = dateString.split("-")
        val year = dateArr[0]
        val month = dateArr[1]
        val dateri1 = dateArr[2]
        var dateri2 = dateArr[2]
        val hour1 = dateArr[3].toInt()
        var hour2 = hour1+1
        if (hour1 == 24) {
            hour2 = 1
            dateri2 = dateri1+1
        }

        val timestr1 = year+"-"+month+"-" + dateri1 + "-" + hour1
        val timestr2 = year+"-"+month+"-" + dateri2 + "-" + hour2
        val timestamp1 = formatter.parse(timestr1).time
        val timestamp2 = formatter.parse(timestr2).time

        val minWeiyi1 = timestamp1 shl 6
        val serialWeiyi1 = serial_number shl 3
        val wResult1 = serialWeiyi1 + minWeiyi1
        val sharesult1 = shaEncode(wResult1.toString())
        val numberResult1 = keepDigital(sharesult1)
        val resultChuli1 = resultChuli(numberResult1)

        val minWeiyi2 = timestamp2 shl 6
        val serialWeiyi2 = serial_number shl 3
        val wResult2 = serialWeiyi2 + minWeiyi2
        val sharesult2 = shaEncode(wResult2.toString())
        val numberResult2 = keepDigital(sharesult2)
        val resultChuli2 = resultChuli(numberResult2)

        return arrayOf(resultChuli1,resultChuli2)
    }


    private fun resultChuli(numberResult: String):String {
        val length = numberResult.length
        if (length>=6) {
            return numberResult.substring(length-6)
        }else {
            val buchong = getBuchong(numberResult)
            return buchong.substring(length-6)
        }
    }

    private fun getBuchong(numberResult: String):String {
        val toInt = numberResult.toInt()
        val shlResult = toInt shl 16
        if (shlResult.toString().length>=6) {
            return shlResult.toString()
        }else {
            return getBuchong(shlResult.toString())
        }
    }

    private fun keepDigital(oldString: String): String {
        val newString = StringBuffer()
        val matcher = Pattern.compile("\\d").matcher(oldString)
        while (matcher.find()) {
            newString.append(matcher.group())
        }
        return newString.toString()
    }

    private fun shaEncode(inStr: String): String {
        val messageDigest = MessageDigest.getInstance("md5")
        val byteArray = inStr.toByteArray(charset("UTF-8"))
        val md5Bytes: ByteArray = messageDigest.digest(byteArray)
        val hexValue = StringBuffer()
        for (i in md5Bytes.indices) {
            val md5Byte = md5Bytes[i].toInt() and 0xff
            if (md5Byte < 16) {
                hexValue.append("0")
            }
            hexValue.append(Integer.toHexString(md5Byte))
        }
        return hexValue.toString()
    }
}