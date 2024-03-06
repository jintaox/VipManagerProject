package com.jintao.vipmanager.utils

import java.math.BigDecimal

class CalculatorUtils {

    companion object {
        private var mInstance : CalculatorUtils? = null
        @Synchronized
        fun getInstence(): CalculatorUtils {
            if (mInstance ==null) {
                mInstance = CalculatorUtils()
            }
            return mInstance!!
        }
    }

    /**
     * 加
     */
    fun add(num1:Float,num2:Float):BigDecimal {
        val nstr1 = BigDecimal(num1.toString())
        val nstr2 = BigDecimal(num2.toString())
        return nstr1.add(nstr2)
    }

    /**
     * 减
     */
    fun subtract(num1:Float,num2:Float):BigDecimal {
        val nstr1 = BigDecimal(num1.toString())
        val nstr2 = BigDecimal(num2.toString())
        return nstr1.subtract(nstr2)
    }

    fun subtract(num1:Float,num2:Int):BigDecimal {
        val nstr1 = BigDecimal(num1.toString())
        val nstr2 = BigDecimal(num2)
        return nstr1.subtract(nstr2)
    }

    /**
     * 乘
     */
    fun multiply(num1:Float,num2:Float):BigDecimal {
        val nstr1 = BigDecimal(num1.toString())
        val nstr2 = BigDecimal(num2.toString())
        return nstr1.multiply(nstr2)
    }

    /**
     * 除
     */
    fun divide(num1:Float,num2:Float):BigDecimal {
        val nstr1 = BigDecimal(num1.toString())
        val nstr2 = BigDecimal(num2.toString())
        return nstr1.divide(nstr2)
    }
}