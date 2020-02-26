package com.yf.verify.fingerprint.factory

/**
 * 通过策略模式，进行指纹验证的弹出
 */
interface BaseBiometricPrompt {
    fun show()
}