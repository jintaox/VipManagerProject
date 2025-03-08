package com.jintao.vipmanager.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract  class BaseFragment<T : ViewBinding> :Fragment() {

    lateinit var mBinding: T
    lateinit var mBaseContext:Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mBaseContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = getViewBinding(inflater,container)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        recoveryPageDate(savedInstanceState)
        initData()
    }

    abstract fun initData()

    abstract fun initListener()

    protected open fun recoveryPageDate(savedInstanceState: Bundle?) {

    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): T
}