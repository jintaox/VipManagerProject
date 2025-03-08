package com.jintao.vipmanager.database

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.jintao.vipmanager.base.IUiView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

fun <T> launchFlow(
    requestBlock: suspend () -> T,
    startCallback: (() -> Unit)? = null,
    completeCallback: (() -> Unit)? = null,
): Flow<T> {
    return flow {
        emit(requestBlock())
    }.onStart {
        startCallback?.invoke()
    }.onCompletion {
        completeCallback?.invoke()
    }
}

/**
 * 请求 需要声明LiveData && 带Loading
 */
fun IUiView.launchWithFlowAndLoading(requestBlock: suspend () -> Unit) {
    lifecycleScope.launch {
        flow {
            emit(requestBlock())
        }.onStart {
            showLoadingDialog()
        }.onCompletion {
            dismissLoadingDialog()
        }.collect()
    }
}

/**
 * 请求 需要声明LiveData && 不带Loading
 */
fun IUiView.launchWithFlowNotLoading(requestBlock: suspend () -> Unit) {
    lifecycleScope.launch {
        flow {
            emit(requestBlock())
        }.collect()
    }
}

/**
 * 请求 不需要声明LiveData && 带Loading
 * isShowErrorToast:默认弹Loading就弹Toast
 */
fun <T> IUiView.launchWithLoadingNotFlow(
    requestBlock: suspend () -> T,
    listenerBuilder: OnDbResultListener<T>.() -> Unit,
) {
    lifecycleScope.launch {
        launchFlow(requestBlock, { showLoadingDialog() }, { dismissLoadingDialog() }).collect { result ->
            val listener = OnDbResultListener<T>().also(listenerBuilder)
            listener.onSuccess(result)
        }
    }
}

/**
 * 请求 不需要声明LiveData && 不带Loading
 */
fun <T> IUiView.launchWithNotLoadingFlow(
    requestBlock: suspend () -> T,
    listenerBuilder: OnDbResultListener<T>.() -> Unit,
) {
    lifecycleScope.launch {
        launchFlow(requestBlock).collect { result ->
            val listener = OnDbResultListener<T>().also(listenerBuilder)
            listener.onSuccess(result)
        }
    }
}

fun <T> Flow<T>.collectIn(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    listenerBuilder: OnDbResultListener<T>.() -> Unit,
): Job = lifecycleOwner.lifecycleScope.launch {
    flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collect { result ->
        val listener = OnDbResultListener<T>().also(listenerBuilder)
        listener.onSuccess(result)
    }
}

class OnDbResultListener<T> {
    var onSuccess: (data: T) -> Unit = {}
}


