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

    public static final String SECRET_MESSAGE = "Very secret message";
    public static final String DIALOG_FRAGMENT_TAG = "FingerprintAuthenticationDialogFragment";

    private FingerprintCharacterStepBuilder() {
    }

    public static KeyStoreStep newBuilder() {
        return new FingerprintCharacterSteps();
    }

    public interface KeyStoreStep {
        KeyGeneratorStep keyStore();
    }

    public interface KeyGeneratorStep {
        CipherStep keyGenerator();
    }

    public interface CipherStep {
        SecretMessage cipher();
    }

    public interface SecretMessage {
        FingerprintCharacterSteps secretMessage(String message);

    }

    public interface DefaultKeyName {
        DialogFragmentTag defaultKeyName();
    }

    public interface KeyNameNotInvalidated {
        DialogFragmentTag keyNameNotInvalidated();
    }

    public interface DialogFragmentTag {
        FingerprintCallback dialogFragmentTag(String tag);
    }

    public interface FingerprintBuildStep {
        FingerprintCharacter build();
    }

    public interface FingerprintCallback {
        FingerprintBuildStep setCallback(FingerprintAuthenticatedCallback callback);
    }


    public static class FingerprintCharacterSteps implements KeyStoreStep, KeyGeneratorStep, CipherStep, SecretMessage
            , DefaultKeyName, KeyNameNotInvalidated, DialogFragmentTag, FingerprintCallback, FingerprintBuildStep {
        private KeyStore keyStore;
        private KeyGenerator keyGenerator;
        private Cipher cipher;
        private String secretMessage;
        private String defaultKeyName;//默认的健
        private String keyNameNotInvalidated; //特定的键
        private String dialogFragmentTag;
        private FingerprintAuthenticatedCallback callback;

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
            if (!TextUtils.isEmpty(secretMessage)) {
                character.setSecretMessage(secretMessage);
            }
            if (!TextUtils.isEmpty(defaultKeyName)) {
                character.setDefaultKeyName(defaultKeyName);
            }
            if (!TextUtils.isEmpty(keyNameNotInvalidated)) {
                character.setKeyNameNotInvalidated(keyNameNotInvalidated);
            }
            if (!TextUtils.isEmpty(dialogFragmentTag)) {
                character.setDialogFragmentTag(dialogFragmentTag);
            }
            if (null != callback) {
                character.setCallback(callback);
            }
            return character;
        }

        @Override
        public KeyGeneratorStep keyStore() {
            try {
                this.keyStore = KeyStore.getInstance("AndroidKeyStore"); //获取密钥库
            } catch (KeyStoreException e) {
                //  获取密码验证失败  Failed to get an instance of KeyStore
            }
            return this;
        }

        @Override
        public CipherStep keyGenerator() {
            try {
                this.keyGenerator = KeyGenerator
                        .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                //  2018/11/23 Failed to get an instance of KeyGenerator  获取KeyGenerator实例失败
            }
            return this;
        }

        @Override
        public SecretMessage cipher() {
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
        public FingerprintCharacterSteps secretMessage(String message) {
            this.secretMessage = message;
            return this;
        }

        @Override
        public DialogFragmentTag defaultKeyName() {
            this.defaultKeyName = "default_key";
            return this;
        }

        @Override
        public DialogFragmentTag keyNameNotInvalidated() {
            this.keyNameNotInvalidated = "key_not_invalidated";
            return this;
        }

        @Override
        public FingerprintCallback dialogFragmentTag(String tag) {
            this.dialogFragmentTag = tag;
            return this;
        }

        @Override
        public FingerprintBuildStep setCallback(FingerprintAuthenticatedCallback callback) {
            this.callback = callback;
            return this;
        }
    }
}
