package com.yf.verify.fingerprint.factory;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.CancellationSignal;

import com.yf.verify.callback.FingerprintAuthenticatedCallback;

@TargetApi(Build.VERSION_CODES.P)
public class BaseBiometricPrompt29 implements BaseBiometricPrompt {
    private CancellationSignal mCancellationSignal;
    private BiometricPrompt mBiometricPrompt;
    private FragmentActivity activity;
    private FingerprintAuthenticatedCallback callback;

    public BaseBiometricPrompt29(FragmentActivity activity, final FingerprintAuthenticatedCallback callback) {
        this.activity = activity;
        this.callback = callback;
        mCancellationSignal = new CancellationSignal();
        mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                if (null != callback) {
                    callback.onFingerprintCancel();
                }
            }
        });
        mBiometricPrompt = new BiometricPrompt.Builder(activity)
                .setNegativeButton("取消", activity.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (null != callback) {
                            callback.onFingerprintCancel();
                        }
                    }
                }).setTitle("指纹验证")
                .build();
    }

    @Override
    public void show() {
        mBiometricPrompt.authenticate(mCancellationSignal, activity.getMainExecutor(), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
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
        });
    }
}
