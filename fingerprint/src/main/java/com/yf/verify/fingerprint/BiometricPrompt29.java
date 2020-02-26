package com.yf.verify.fingerprint;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.CancellationSignal;

import com.yf.verify.base.BaseBiometricPrompt;
import com.yf.verify.callback.FingerprintAuthenticatedCallback;

@TargetApi(Build.VERSION_CODES.P)
public class BiometricPrompt29 extends BiometricPrompt.AuthenticationCallback implements BaseBiometricPrompt, DialogInterface.OnClickListener {
    private CancellationSignal mCancellationSignal;
    private BiometricPrompt mBiometricPrompt;
    private FragmentActivity activity;
    private FingerprintAuthenticatedCallback callback;

    public BiometricPrompt29(FragmentActivity activity, final FingerprintAuthenticatedCallback callback) {
        this.activity = activity;
        this.callback = callback;
        mCancellationSignal = new CancellationSignal();
        mBiometricPrompt = new BiometricPrompt.Builder(activity)
                .setNegativeButton("取消", activity.getMainExecutor(), this)
                .setTitle("指纹验证")
                .build();
    }

    @Override
    public void show() {
        mBiometricPrompt.authenticate(mCancellationSignal, activity.getMainExecutor(),this);
    }

    @Override
    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        if (null != callback) {
            callback.onFingerprintSucceed();
        }
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        if (null != callback) {
            callback.onFingerprintFailed();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (null != callback) {
            callback.onFingerprintCancel();
        }
    }
}
