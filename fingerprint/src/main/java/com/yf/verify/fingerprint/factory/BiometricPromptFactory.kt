package com.yf.verify.fingerprint.factory

import android.os.Build
import android.support.v4.app.FragmentActivity
import com.yf.verify.callback.FingerprintAuthenticatedCallback
import com.yf.verify.callback.UpdateFingerprintCallback
import com.yf.verify.fingerprint.FingerprintAuthenticationDialogFragment
import javax.crypto.Cipher

class BiometricPromptFactory(var activity: FragmentActivity, var cipher: Cipher, var stage: FingerprintAuthenticationDialogFragment.Stage, var updateFingerprintCallback: UpdateFingerprintCallback, var fingerprintCallback: FingerprintAuthenticatedCallback) {
    val prompt = hashMapOf<Int, BaseBiometricPrompt>()

    init {
        prompt[Build.VERSION_CODES.P] = BaseBiometricPrompt29(activity, fingerprintCallback)
        prompt[Build.VERSION_CODES.M] = BaseBiometricPrompt28(activity, cipher, stage, updateFingerprintCallback, fingerprintCallback)
    }

    fun execute(version: Int) {
        if (version >= Build.VERSION_CODES.P) {
            prompt[Build.VERSION_CODES.P]?.show()
        } else {
            prompt[Build.VERSION_CODES.M]?.show()
        }
    }
}