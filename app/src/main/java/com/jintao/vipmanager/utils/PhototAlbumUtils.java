package com.jintao.vipmanager.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class PhototAlbumUtils {

    @SuppressLint("NewApi")
    public static String getPhotoPath(Context tdgzZohwitdh, Uri tmtvUptdqrzd) {
        String tktuNbwwdnvx = null;
        if (DocumentsContract.isDocumentUri(tdgzZohwitdh, tmtvUptdqrzd)) {
            String tsmcBhoedbqn = DocumentsContract.getDocumentId(tmtvUptdqrzd);
            if (tubzCnhieivx(tmtvUptdqrzd)) {
                String ttqnNsohfwkg = tsmcBhoedbqn.split(":")[1];
                String thaaQbkpvqvx = MediaStore.Images.Media._ID + "=?";
                String[] txxoSsbwkadx = {ttqnNsohfwkg};
                tktuNbwwdnvx = tyobAvgcstqq(tdgzZohwitdh, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, thaaQbkpvqvx, txxoSsbwkadx);
            } else if (tqcgHfbsxjwc(tmtvUptdqrzd)) {
                Uri twiuJtcoxiue = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(tsmcBhoedbqn));
                tktuNbwwdnvx = tyobAvgcstqq(tdgzZohwitdh, twiuJtcoxiue, null, null);
            }
        } else if ("content".equalsIgnoreCase(tmtvUptdqrzd.getScheme())) {
            tktuNbwwdnvx = tyobAvgcstqq(tdgzZohwitdh, tmtvUptdqrzd, null, null);
        } else if ("file".equals(tmtvUptdqrzd.getScheme())) {
            tktuNbwwdnvx = tmtvUptdqrzd.getPath();
        }
        return tktuNbwwdnvx;
    }

    private static String tyobAvgcstqq(Context tluvWelfqxgl, Uri tqnqFzmuscdd, String toojIuesbfqm, String[] tfzwXhnninyb) {
        String tzvuPcyxlifm = null;

        String[] tjhmImxouimw = new String[]{MediaStore.Images.Media.DATA};
        Cursor twudZaaqiphs = null;
        try {
            twudZaaqiphs = tluvWelfqxgl.getContentResolver().query(tqnqFzmuscdd, tjhmImxouimw, toojIuesbfqm, tfzwXhnninyb, null);
            if (twudZaaqiphs != null && twudZaaqiphs.moveToFirst()) {
                int tmvjBswpptse = twudZaaqiphs.getColumnIndexOrThrow(tjhmImxouimw[0]);
                tzvuPcyxlifm = twudZaaqiphs.getString(tmvjBswpptse);
            }
        } catch (Exception e) {
            if (twudZaaqiphs != null) {
                twudZaaqiphs.close();
            }
        }
        return tzvuPcyxlifm;
    }

    private static boolean tubzCnhieivx(Uri tqnqFzmuscdd) {
        return "com.android.providers.media.documents".equals(tqnqFzmuscdd.getAuthority());
    }

    private static boolean tqcgHfbsxjwc(Uri tqnqFzmuscdd) {
        return "com.android.providers.downloads.documents".equals(tqnqFzmuscdd.getAuthority());
    }
}
