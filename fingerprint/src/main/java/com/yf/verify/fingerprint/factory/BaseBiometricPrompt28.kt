package com.yf.verify.fingerprint.factory

import android.annotation.TargetApi
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.support.v4.app.FragmentActivity
import com.yf.verify.callback.FingerprintAuthenticatedCallback
import com.yf.verify.callback.UpdateFingerprintCallback
import com.yf.verify.fingerprint.FingerprintAuthenticationDialogFragment
import javax.crypto.Cipher

@TargetApi(Build.VERSION_CODES.M)
class BaseBiometricPrompt28(var activity: FragmentActivity, var cipher: Cipher, var stage: FingerprintAuthenticationDialogFragment.Stage,
                            var updateFingerprintCallback: UpdateFingerprintCallback, var fingerprintCallback: FingerprintAuthenticatedCallback) : BaseBiometricPrompt {

    override fun show() {
        val fragment = FingerprintAuthenticationDialogFragment.newInstance()
        fragment.setCryptoObject(FingerprintManager.CryptoObject(cipher))
        fragment.setStage(stage)
        fragment.setUpdateFingerprintCallback(updateFingerprintCallback)
        fragment.setCallback(fingerprintCallback)
        fragment.show(activity?.supportFragmentManager!!, "FingerprintAuthenticationDialogFragment")
    }

}