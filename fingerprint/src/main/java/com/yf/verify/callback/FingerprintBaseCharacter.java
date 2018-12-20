package com.yf.verify.callback;

import android.hardware.fingerprint.FingerprintManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import javax.crypto.Cipher;

/**
 * @author yangfeng
 * @create 2018/11/28 11:00
 * @Describe
 */
public interface FingerprintBaseCharacter extends BaseCharacter{

    void show(FragmentManager fragmentManager);

    void onCreateKey(String keyName, boolean invalidatedByBiometricEnrollment);

    boolean initCipher(Cipher cipher, String keyName);

}
