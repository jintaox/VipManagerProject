package com.jintao.vipmanager.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.jintao.vipmanager.BuildConfig;

/**
 * Created by zhanghui on 2021/5/24 11:42
 * Describe:
 */
public class OpenSettingUtil {

    /**
     * 打开设置页面
     *
     * @param activity
     * @param code
     */
    public static void openSettingPage(Activity activity, int code) {
        String board = Build.BOARD;
        if ("redmi".equals(board.toLowerCase()) || "xiaomi".equals(board.toLowerCase())) {
            miuiPermis(activity, code);
        } else if ("meizu".equals(board.toLowerCase())) {
            meizuPermis(activity, code);
        } else if ("huawei".equals(board.toLowerCase()) || "honor".equals(board.toLowerCase())) {
            huaweiPermis(activity, code);
        } else {
            activity.startActivityForResult(openDetailSeting(activity, code), code);
        }
    }

    private static void miuiPermis(Activity activity, int intent) {
        try { // MIUI 8
            miui_one(activity);
        } catch (Exception e) {
            miui_two(activity, intent);
        }
    }

    private static void miui_one(Activity activity) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
        );
        intent.putExtra("extra_pkgname", activity.getPackageName());
        activity.startActivity(intent);
    }

    private static void miui_two(Activity activity, int intent) {
        try { // MIUI 5/6/7
            Intent intent2 = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent2.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
            );
            intent2.putExtra("extra_pkgname", activity.getPackageName());
            activity.startActivity(intent2);

        } catch (Exception e) { // 否则跳转到应用详情
            activity.startActivity(openDetailSeting(activity, intent));
        }
    }

    private static void huaweiPermis(Activity activity, int intent) {
        try {
            Intent i = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.putExtra("packageName", BuildConfig.APPLICATION_ID);
            activity.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
            activity.startActivity(openDetailSeting(activity, intent));
        }
    }

    private static void meizuPermis(Activity activity, int intent) {
        try {
            Intent intent3 = new Intent();
            intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName componentName = new ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainUHActivity"
            );
            intent3.setComponent(componentName);
            activity.startActivity(intent3);
        } catch (Exception e) {
            e.printStackTrace();
            activity.startActivity(openDetailSeting(activity, intent));
        }
    }


    /**
     * 获取应用详情页面intent
     * 如果找不到要跳转的界面，也可以先把用户引导到系统设置页面
     */
    private static Intent openDetailSeting(Activity activity, int intent) {
        Intent intent4 = new Intent();
        intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent4.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent4.setData(Uri.fromParts(
                    "package",
                    activity.getPackageName(),
                    null
            ));

        } else if (Build.VERSION.SDK_INT <= 8) {
            intent4.setAction(Intent.ACTION_VIEW);
            intent4.setClassName(
                    "com.android.settings",
                    "com.android.settings.InstalledAppDetails"
            );
            intent4.putExtra(
                    "com.android.settings.ApplicationPkgName",
                    activity.getPackageName()
            );

        }
        return intent4;
    }
}
