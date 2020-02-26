package com.yf.verify.callback;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.yf.verify.base.BaseCharacter;

/**
 * @author yangfeng
 * @create 2018/11/29 09:52
 * @Describe
 */
public interface CodedLockBaseCharacter extends BaseCharacter {
    boolean onValidate();

    boolean isKeyguardSecure();

    void showAuthenticationScreen(Activity activity);

    void onDestroy();

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}
