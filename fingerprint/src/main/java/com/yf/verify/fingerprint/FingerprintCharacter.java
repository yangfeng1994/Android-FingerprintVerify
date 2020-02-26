package com.yf.verify.fingerprint;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.FragmentActivity;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import com.yf.verify.callback.FingerprintAuthenticatedCallback;
import com.yf.verify.callback.FingerprintBaseCharacter;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


/**
 * @author yangfeng
 * @create 2018/11/28 10:57
 * @Describe
 */
public class FingerprintCharacter implements FingerprintBaseCharacter {

    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private String mKeystoreAlias;
    private FingerprintAuthenticatedCallback callback;
    private FingerprintManagerCompat mFingerprintManager;

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public Cipher getCipher() {
        return cipher;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }


    public String getKeystoreAlias() {
        return mKeystoreAlias;
    }

    public void setKeystoreAlias(String keystoreAlias) {
        this.mKeystoreAlias = keystoreAlias;
    }


    public FingerprintAuthenticatedCallback getFingerprintCallback() {
        return callback;
    }

    public void setFingerprintCallback(FingerprintAuthenticatedCallback callback) {
        this.callback = callback;
    }

    /**
     * 显示popuwindow
     *
     * @param activity
     */
    @Override
    public void show(FragmentActivity activity) {
        Cipher cipher = getCipher();
        String keystoreAlias = getKeystoreAlias();
        boolean isCipher = initCipher(cipher, keystoreAlias);
        mFingerprintManager = getFingerprintManager(activity);
        if (!isFingerprintAuthAvailable()) {
            if (null != callback) {
                callback.onNoEnrolledFingerprints();
            }
            return;
        }
        if (!isCipher) {
            onCreateKey(keystoreAlias, true);
        }
        BiometricPromptFactory factory = new BiometricPromptFactory(activity, cipher, mFingerprintManager, getFingerprintCallback());
        factory.execute(Build.VERSION.SDK_INT);
    }


    /**
     * 判断是否录入指纹或者是否支持指纹解锁
     *
     * @return
     */
    public boolean isFingerprintAuthAvailable() {
        // 判断是否录入指纹
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (null != mFingerprintManager && mFingerprintManager.isHardwareDetected()) {
                if (mFingerprintManager.hasEnrolledFingerprints()) { // 是否录入指纹
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 在Android密钥存储库中创建一个对称密钥，该密钥只能在用户拥有之后使用
     * *指纹验证。
     *
     * @param keyName                          要创建的键的名称
     * @param invalidatedByBiometricEnrollment 如果传递{@code false}，录入新的指纹创建的密钥不会失效
     *                                         * {@code true}不改变行为
     *                                         *(如有新指纹，此密码将无效。
     *                                         *该应用可以在Android N开发者预览版上运行。
     */
    @Override
    public void onCreateKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        //指纹登记流程。这是您要求用户设置指纹的地方
        try {
            getKeyStore().load(null);
            //在Android密钥库中设置条目的别名，该密钥将出现在该条目中
            //以及构造器的构造函数中的约束(目的)
            KeyGenParameterSpec.Builder builder = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                builder = new KeyGenParameterSpec.Builder(keyName,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        // 要求用户使用指纹进行身份验证以授权每次使用
                        //钥匙的钥匙
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (null != builder) {
                        builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
                    }
                }
                if (null != getKeyGenerator()) {
                    getKeyGenerator().init(builder.build());
                    getKeyGenerator().generateKey();
                }
            }


            /**
             *  这是一个避免API级别< 24的设备崩溃的解决方案
             *  因为KeyGenParameterSpec。构建器#setInvalidatedByBiometricEnrollment只是
             *  在API级别+24上可见。
             *  理想情况下，KeyGenParameterSpec应该有一个compat库。构建器,但到目前还没有。
             */
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FingerprintManagerCompat getFingerprintManager(Context context) {
        FingerprintManagerCompat fingerprintManager = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fingerprintManager = FingerprintManagerCompat.from(context);
            }
        } catch (Throwable e) {
        }
        return fingerprintManager;
    }

    /**
     * 初始化KeyStore
     *
     * @param cipher
     * @param keyName
     * @return
     */
    @Override
    public boolean initCipher(Cipher cipher, String keyName) {
        try {
            getKeyStore().load(null);
            SecretKey key = (SecretKey) getKeyStore().getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {

            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
