package com.yf.verify.callback;


/**
 * @author yangfeng
 * @create 2018/11/26 11:15
 * @Describe 指纹或密码验证的callback
 */
public interface FingerprintAuthenticatedCallback {

    /**
     * 验证成功
     */
    void onFingerprintSucceed();

    /**
     * 验证失败
     */
    void onFingerprintFailed();

    /**
     * 取消验证
     */
    void onFingerprintCancel();

    /**
     * 没有录入指纹
     */
    void noEnrolledFingerprints();

}
