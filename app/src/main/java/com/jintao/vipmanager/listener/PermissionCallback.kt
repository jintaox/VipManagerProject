package com.jintao.vipmanager.listener

interface PermissionCallback {
    fun onPermissionsResult(requestCode:Int, permStatus:Int)
}