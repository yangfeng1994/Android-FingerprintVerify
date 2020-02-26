package com.yf.verify.fingerprint.factory;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import com.yf.verify.callback.FingerprintAuthenticatedCallback;

import javax.crypto.Cipher;

public class BiometricPromptFactory {
    private FragmentActivity activity;
    private Cipher cipher;
    private FingerprintManagerCompat mFingerprintManager;
    private FingerprintAuthenticatedCallback fingerprintCallback;
    private BaseBiometricPrompt prompt;

    public BiometricPromptFactory(FragmentActivity activity, Cipher cipher, FingerprintManagerCompat mFingerprintManager, FingerprintAuthenticatedCallback fingerprintCallback) {
        this.activity = activity;
        this.cipher = cipher;
        this.mFingerprintManager = mFingerprintManager;
        this.fingerprintCallback = fingerprintCallback;
    }


    public void execute(int version) {
        if (version >= Build.VERSION_CODES.P) {
            prompt = new BaseBiometricPrompt29(activity, fingerprintCallback);
//            prompt = new BaseBiometricPrompt28(activity, cipher, mFingerprintManager, fingerprintCallback);
        } else if (version >= Build.VERSION_CODES.M) {
            prompt = new BaseBiometricPrompt28(activity, cipher, mFingerprintManager, fingerprintCallback);
        } else {
            if (null != fingerprintCallback) {
                fingerprintCallback.noEnrolledFingerprints();
            }
            return;
        }
        prompt.show();
    }
}
