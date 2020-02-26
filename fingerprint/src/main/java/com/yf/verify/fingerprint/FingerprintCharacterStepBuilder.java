package com.yf.verify.fingerprint;

import android.security.keystore.KeyProperties;
import android.text.TextUtils;

import com.yf.verify.callback.FingerprintAuthenticatedCallback;

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

/**
 * @author yangfeng
 * @create 2018/11/28 11:12
 * @Describe
 */
public final class FingerprintCharacterStepBuilder {

    private FingerprintCharacterStepBuilder() {
    }

    public static FingerprintCharacterSteps newBuilder() {
        return new FingerprintCharacterSteps();
    }

    private interface KeyStoreStep {
        KeyGeneratorStep getKeyStore();
    }

    private interface KeyGeneratorStep {
        CipherStep getKeyGenerator();
    }

    private interface CipherStep {
        FingerprintCharacterSteps getCipher();
    }


    private interface KeystoreAlias {
        FingerprintCallback setKeystoreAlias(String keystoreAlias);
    }

    public interface FingerprintCallback {
        FingerprintBuildStep setFingerprintCallback(FingerprintAuthenticatedCallback callback);
    }

    public interface FingerprintBuildStep {
        FingerprintCharacter build();
    }




    public static class FingerprintCharacterSteps implements KeyStoreStep, KeyGeneratorStep, CipherStep
            , KeystoreAlias,  FingerprintCallback, FingerprintBuildStep {
        private KeyStore keyStore;
        private KeyGenerator keyGenerator;
        private Cipher cipher;
        private String mKeystoreAlias;//默认的健
        private FingerprintAuthenticatedCallback callback;

        public FingerprintCharacterSteps() {
            KeyGeneratorStep keyStore = getKeyStore(); //获取密钥库
            CipherStep keyGenerator = keyStore.getKeyGenerator();//获取KeyGenerator实例，用于密钥库的初始化
            keyGenerator.getCipher(); //获取密码加密和解密的加密密码的功能的实例
        }

        @Override
        public FingerprintCharacter build() {
            FingerprintCharacter character = new FingerprintCharacter();
            if (null != keyStore) {
                character.setKeyStore(keyStore);
            }
            if (null != keyGenerator) {
                character.setKeyGenerator(keyGenerator);
            }
            if (null != cipher) {
                character.setCipher(cipher);
            }
            if (!TextUtils.isEmpty(mKeystoreAlias)) {
                character.setKeystoreAlias(mKeystoreAlias);
            }
            if (null != callback) {
                character.setFingerprintCallback(callback);
            }
            return character;
        }

        @Override
        public KeyGeneratorStep getKeyStore() {
            try {
                this.keyStore = KeyStore.getInstance("AndroidKeyStore"); //获取密钥库
            } catch (KeyStoreException e) {
                //  获取密码验证实例失败
            }
            return this;
        }

        @Override
        public CipherStep getKeyGenerator() {
            try {
                this.keyGenerator = KeyGenerator
                        .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                //  2018/11/23   获取KeyGenerator实例失败
            }
            return this;
        }

        @Override
        public FingerprintCharacterSteps getCipher() {
            try {
                this.cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + File.separator
                        + KeyProperties.BLOCK_MODE_CBC + File.separator
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                //  获取密码实例失败
                e.printStackTrace();
            }
            return this;
        }


        @Override
        public FingerprintBuildStep setFingerprintCallback(FingerprintAuthenticatedCallback callback) {
            this.callback = callback;
            return this;
        }

        @Override
        public FingerprintCallback setKeystoreAlias(String keystoreAlias) {
            this.mKeystoreAlias = keystoreAlias;
            return this;
        }
    }
}
