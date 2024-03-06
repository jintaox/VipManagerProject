package com.jintao.vipmanager.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.FileProvider;

import com.jintao.vipmanager.MyApplication;
import com.jintao.vipmanager.listener.OnPictureCompressListener;

import java.io.File;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class PictureUtils {

    private static PictureUtils mInstance = null;

    private PictureUtils() {
    }

    public static PictureUtils getInstence() {
        if (mInstance == null) {
            synchronized (PictureUtils.class) {
                mInstance = new PictureUtils();
            }
        }
        return mInstance;
    }

    public void takeCameraPicture(Activity context, ActivityResultLauncher<Intent> pictureLauncher, String photoPath) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File outputImage = new File(photoPath);
        if (outputImage.exists()) outputImage.delete();
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", outputImage);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            fileUri = Uri.fromFile(outputImage);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        pictureLauncher.launch(intent);
    }

    public void takeCameraVideo(Activity context, ActivityResultLauncher<Intent> pictureLauncher, String photoPath) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File outputImage = new File(photoPath);
        if (outputImage.exists()) outputImage.delete();
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", outputImage);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            fileUri = Uri.fromFile(outputImage);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        pictureLauncher.launch(intent);
    }

    public void openPhotoAlbum(Activity activity, ActivityResultLauncher<Intent> pictureLauncher) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            pictureLauncher.launch(intent);
        } else {
            pictureLauncher.launch(intent);
        }
    }

    public void compressPictureFile(String filePath, String storePath, OnPictureCompressListener listener) {
        Luban.with(MyApplication.Companion.getMContext())
                .load(filePath)
                .setTargetDir(storePath)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        listener.onResultSuccess(file.getPath());
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onResultFail();
                    }
                }).launch();
    }
}
