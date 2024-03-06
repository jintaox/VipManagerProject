package com.jintao.vipmanager.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jintao.vipmanager.listener.PermissionCallback;

public class PermissionsUtil {

    public static final int GRANT = 101;
    public static final int DENIED = 102;
    public static final int NOASK = 103;
    private PermissionCallback listener;

    private static PermissionsUtil mInstance = null;

    private PermissionsUtil(){}
    public static PermissionsUtil getInstence() {
        if (mInstance==null) {
            synchronized (PermissionsUtil.class) {
                mInstance = new PermissionsUtil();
            }
        }
        return mInstance;
    }

    public void checkAndRequestPerm(Activity activity, int permissionCode, PermissionCallback listener, String... perms) {
        boolean havePermission = checkPermissions(activity, perms);
        if (havePermission) {
            listener.onPermissionsResult(permissionCode, GRANT);
        }else {
            requestPermissions(activity,permissionCode,listener,perms);
        }
    }

    public void requestPermissions(Activity activity, int permissionCode, PermissionCallback listener, String... perms) {
        ActivityCompat.requestPermissions(activity, perms, permissionCode);
        this.listener = listener;
    }

    public void resetPermissionListener(PermissionCallback listener) {
        this.listener = listener;
    }

    public boolean checkPermissions(Activity activity, String... perms) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(activity, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults, @NonNull Activity context) {
        int status = -1;
        if (listener!=null) {
            for (int i = 0;i < grantResults.length;i++) {
                int grant = grantResults[i];
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permissions[i])) {
                        status = NOASK;
                    }else {
                        if (status != NOASK) {
                            status = DENIED;
                        }
                    }
                }else {
                    if (status != NOASK &&status != DENIED) {
                        status = GRANT;
                    }
                }
            }
            listener.onPermissionsResult(requestCode,status);
        }
    }
}
