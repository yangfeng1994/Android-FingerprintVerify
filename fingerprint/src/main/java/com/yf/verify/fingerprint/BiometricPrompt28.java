package com.yf.verify.fingerprint;

import android.support.v4.app.FragmentActivity;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import com.yf.verify.base.BaseBiometricPrompt;
import com.yf.verify.callback.FingerprintAuthenticatedCallback;
import com.yf.verify.fingerprint.FingerprintBottomDialogFragment;

import javax.crypto.Cipher;

public class BiometricPrompt28 implements BaseBiometricPrompt {
    private FragmentActivity activity;
    private Cipher cipher;
    private FingerprintManagerCompat mFingerprintManager;
    private FingerprintAuthenticatedCallback fingerprintCallback;

    public BiometricPrompt28(FragmentActivity activity, Cipher cipher, FingerprintManagerCompat mFingerprintManager, FingerprintAuthenticatedCallback fingerprintCallback) {
        this.activity = activity;
        this.cipher = cipher;
        this.mFingerprintManager = mFingerprintManager;
        this.fingerprintCallback = fingerprintCallback;
    }

    @Override
    public void show() {
        if (null == mFingerprintManager) {
            if (null != fingerprintCallback) {
                fingerprintCallback.onNoEnrolledFingerprints();
            }
            return;
        }
        FingerprintBottomDialogFragment fragment = FingerprintBottomDialogFragment.newInstance();
        fragment.setFingerprintManager(mFingerprintManager);
        fragment.setCryptoObject(new FingerprintManagerCompat.CryptoObject(cipher));
        fragment.setCallback(fingerprintCallback);
        fragment.show(activity.getSupportFragmentManager(), "FingerprintBottomDialogFragment");
    }
}
