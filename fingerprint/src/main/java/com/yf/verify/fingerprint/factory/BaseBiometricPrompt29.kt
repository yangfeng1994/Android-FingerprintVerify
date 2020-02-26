package com.yf.verify.fingerprint.factory

import android.annotation.TargetApi
import android.app.Activity
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import android.util.Log
import com.yf.verify.callback.FingerprintAuthenticatedCallback

@TargetApi(Build.VERSION_CODES.P)
class BaseBiometricPrompt29 : BaseBiometricPrompt {

    private var mCancellationSignal: CancellationSignal? = null
    private var mBiometricPrompt: BiometricPrompt? = null
    private var activity: Activity? = null
    private var callback: FingerprintAuthenticatedCallback? = null

    constructor(activity: Activity, callback: FingerprintAuthenticatedCallback) {
        this.callback = callback
        this.activity = activity
        mCancellationSignal = CancellationSignal()
        mBiometricPrompt = BiometricPrompt.Builder(activity)
                .setDescription("支付金额")
                .setNegativeButton("取消支付", activity?.mainExecutor, DialogInterface.OnClickListener { dialog, which -> Log.e("yyyy", "DialogInterface") })
                .setSubtitle("用户进行支付")
                .setTitle("支付")
                .build()

    }

    override fun show() {
        mBiometricPrompt?.authenticate(mCancellationSignal!!, activity?.mainExecutor!!, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                callback?.onFingerprintAuthenticatedSucceed()
            }
        })
    }
}