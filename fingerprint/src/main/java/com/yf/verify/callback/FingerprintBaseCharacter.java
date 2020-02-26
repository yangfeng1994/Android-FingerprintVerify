package com.yf.verify.callback;

import android.support.v4.app.FragmentActivity;

import com.yf.verify.base.BaseCharacter;

import javax.crypto.Cipher;

/**
 * @author yangfeng
 * @create 2018/11/28 11:00
 * @Describe
 */
public interface FingerprintBaseCharacter extends BaseCharacter {

    void show(FragmentActivity activity);

    void onCreateKey(String keyName, boolean invalidatedByBiometricEnrollment);

    boolean initCipher(Cipher cipher, String keyName);

}
