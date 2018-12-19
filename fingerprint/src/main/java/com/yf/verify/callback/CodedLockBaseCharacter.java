package com.yf.verify.callback;

import android.app.Activity;

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
}
