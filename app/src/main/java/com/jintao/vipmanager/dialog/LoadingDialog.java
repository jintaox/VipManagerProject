package com.jintao.vipmanager.dialog;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jintao.vipmanager.R;

/**
 * Author: zhanghui
 * CreateDate: 2024/3/21 9:40
 * Description:
 */
public class LoadingDialog extends Dialog {

    private String text;
    public LoadingDialog(@NonNull Context context, String text) {
        super(context, R.style.MyDialogStyle);
        this.text = text;
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setDimAmount(0f);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setCanceledOnTouchOutside(false);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode==KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.didlog_loading);

        ImageView mIvLoading = findViewById(R.id.iv_loading);
        TextView mTvText = findViewById(R.id.tv_loading_text);
        if (!TextUtils.isEmpty(text)) {
            mTvText.setText(text);
        }
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mIvLoading, "rotation", 0f, 360f);
        animator3.setInterpolator(new LinearInterpolator());
        animator3.setRepeatCount(ValueAnimator.INFINITE);
        animator3.setDuration(1200);
        animator3.start();
    }
}
