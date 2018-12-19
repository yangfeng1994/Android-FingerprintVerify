package com.yf.verify.codedlock;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Log;

import com.yf.verify.callback.CodedLockAuthenticatedCallBack;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;

/**
 * @author yangfeng
 * @create 2018/11/29 09:41
 * @Describe
 */
public final class CodedLockAuthenticatedStepBuilder {

    public static InjectActivity newBuilder() {
        return new CodedLockAuthenticatedCharacterSteps();
    }

    public interface InjectActivity {
        CreateKeyguardManager setActivity(Activity activity);
    }

    public interface CreateKeyguardManager {
        CodedLockName onCreateKeyguardManager();
    }

    public interface CodedLockName {
        AuthenticationDurationSeconds name(String name);
    }

    public interface AuthenticationDurationSeconds {
        CreateKey time(int time);
    }

    public interface CreateKey {
        AuthenticationScreenCallBack onCreateKey();
    }

    public interface AuthenticationScreenCallBack {
        CodedLockAuthenticatedBuildStep onAuthenticationScreenCallBack(CodedLockAuthenticatedCallBack callBack);
    }

    public interface CodedLockAuthenticatedBuildStep {
        CodedLockCharacter build();
    }


    public static class CodedLockAuthenticatedCharacterSteps implements InjectActivity, CreateKeyguardManager, CodedLockName,
            AuthenticationDurationSeconds, CreateKey, AuthenticationScreenCallBack, CodedLockAuthenticatedBuildStep {
        private CodedLockAuthenticatedCallBack callBack;
        private Activity mActivity;

        private KeyguardManager mKeyguardManager;
        /**
         * 我们在Android密钥存储中的密钥的别名。
         */
        private String keyName;

        /**
         * 用户设定的有效时间，如果解锁后，在规定的时间内再次点击，就不会显示解锁失败。
         */
        private int authenticationDurationSeconds;

        @Override
        public CodedLockName onCreateKeyguardManager() {
            mKeyguardManager = (KeyguardManager) mActivity.getSystemService(Context.KEYGUARD_SERVICE);
            return this;
        }

        @Override
        public AuthenticationScreenCallBack onCreateKey() {
            //生成解密支付凭证、令牌等的密钥。
            //这很可能是用户在设置应用程序时的注册步骤。
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

                //在Android密钥库中设置条目的别名，该密钥将出现在该条目中
                //以及构造器的构造函数中的约束(目的)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    keyGenerator.init(new KeyGenParameterSpec.Builder(keyName,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setUserAuthenticationRequired(true)
                            //用户解锁的时长为设置的 authenticationDurationSeconds 秒内有效
                            .setUserAuthenticationValidityDurationSeconds(authenticationDurationSeconds)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build());
                }
                keyGenerator.generateKey();
            } catch (NoSuchAlgorithmException | NoSuchProviderException
                    | InvalidAlgorithmParameterException | KeyStoreException
                    | CertificateException | IOException e) {
                Log.e("yyyy", "创建对称密钥失败");
            }
            return this;
        }

        @Override
        public CodedLockCharacter build() {
            CodedLockCharacter character = new CodedLockCharacter();
            if (null != mActivity) {
                character.setActivity(mActivity);
            }
            if (null != mKeyguardManager) {
                character.setKeyguardManager(mKeyguardManager);
            }
            if (!TextUtils.isEmpty(keyName)) {
                character.setKeyName(keyName);
            }
            if (null != callBack) {
                character.setCallBack(callBack);
            }
            return character;
        }

        @Override
        public AuthenticationDurationSeconds name(String name) {
            keyName = name;
            return this;
        }

        @Override
        public CreateKey time(int time) {
            authenticationDurationSeconds = time;
            return this;
        }

        @Override
        public CreateKeyguardManager setActivity(Activity activity) {
            this.mActivity = activity;
            return this;
        }

        @Override
        public CodedLockAuthenticatedBuildStep onAuthenticationScreenCallBack(CodedLockAuthenticatedCallBack callBack) {
            this.callBack = callBack;
            return this;
        }
    }
}
