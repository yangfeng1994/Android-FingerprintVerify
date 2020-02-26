package com.yf.verify.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

abstract public class BaseDialog extends DialogFragment {

    protected abstract int getLayoutRes();

    public boolean isCancelableOutside() {
        return true;
    }

    public int getDialogWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public int getDialogHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }


    public float dimAmount() {
        return 0.6f;
    }


    public int getGravity() {
        return Gravity.CENTER;
    }

    public int animRes() {
        return 0;
    }

    public Drawable getBackgroundDrawable() {
        return new ColorDrawable(Color.TRANSPARENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutRes = getLayoutRes();
        if (layoutRes > 0) {
            return inflater.inflate(layoutRes, container, false);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Dialog dialog = getDialog();
        if (null == dialog) {
            return;
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //如果isCancelable()是false 则会屏蔽物理返回键
        dialog.setCancelable(isCancelable());
        //如果isCancelableOutside()为false 点击屏幕外Dialog不会消失；反之会消失
        dialog.setCanceledOnTouchOutside(isCancelableOutside());
        //如果isCancelable()设置的是false 会屏蔽物理返回键
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && !isCancelable();
            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onInitFastData();
    }

    /**
     * 初始化数据
     */
    protected abstract void onInitFastData();


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (null == dialog) {
            return;
        }
        Window window = dialog.getWindow();
        if (null == window) {
            return;
        }
        //设置背景色透明
        window.setBackgroundDrawable(getBackgroundDrawable());
        //设置Dialog动画效果
        if (animRes() > 0) {
            window.setWindowAnimations(animRes());
        }
        WindowManager.LayoutParams params = window.getAttributes();
        //设置Dialog的Width
        params.width = getDialogWidth();
        //设置Dialog的Height
        params.height = getDialogHeight();
        //设置屏幕透明度 0.0f~1.0f(完全透明~完全不透明)
        params.dimAmount = dimAmount();
        params.gravity = getGravity();
        window.setAttributes(params);
    }


    public boolean isShowing() {
        Dialog dialog = getDialog();
        if (null == dialog) {
            return false;
        }
        return dialog.isShowing() && isVisible();

    }

}
