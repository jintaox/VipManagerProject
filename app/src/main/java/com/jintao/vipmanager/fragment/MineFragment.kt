package com.jintao.vipmanager.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jintao.vipmanager.base.BaseFragment
import com.jintao.vipmanager.databinding.FragmentMineBinding

/**
 * Author: jintao
 * CreateDate: 2023/8/23 20:45
 * Description:
 */
class MineFragment:BaseFragment<FragmentMineBinding>() {

    override fun initData() {

    }

    override fun initListener() {

    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMineBinding {
        return FragmentMineBinding.inflate(inflater,container,false)
    }
}