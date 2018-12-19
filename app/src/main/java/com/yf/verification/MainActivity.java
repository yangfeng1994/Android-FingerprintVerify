package com.yf.verification;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yf.verify.callback.FingerprintAuthenticatedCallback;
import com.yf.verify.callback.CodedLockAuthenticatedCallBack;
import com.yf.verify.codedlock.CodedLockCharacter;
import com.yf.verify.codedlock.CodedLockAuthenticatedStepBuilder;
import com.yf.verify.fingerprint.FingerprintCharacter;
import com.yf.verify.fingerprint.FingerprintCharacterStepBuilder;
import com.yf.verify.callback.InputPassWordCallback;

public class MainActivity extends AppCompatActivity implements FingerprintAuthenticatedCallback, CodedLockAuthenticatedCallBack {
    private FingerprintCharacter fingerprintAuthenticatedCharacter;
    private CodedLockCharacter codedLockAuthenticatedCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button purchaseButton = findViewById(R.id.purchase_button);
        Button passwordButton = findViewById(R.id.password_button);
        fingerprintAuthenticatedCharacter = FingerprintCharacterStepBuilder
                .newBuilder()
                .keyStore()
                .keyGenerator()
                .cipher()
                .secretMessage(FingerprintCharacterStepBuilder.SECRET_MESSAGE)
                .defaultKeyName() //todo 跟keyNameNotInvalidated任选其一
//                .keyNameNotInvalidated()
                .dialogFragmentTag(FingerprintCharacterStepBuilder.DIALOG_FRAGMENT_TAG)
                .setCallback(this)
                .build();
        codedLockAuthenticatedCharacter = CodedLockAuthenticatedStepBuilder.newBuilder()
                .setActivity(MainActivity.this)
                .onCreateKeyguardManager()
                .name("my_key").time(20)
                .onCreateKey()
                .onAuthenticationScreenCallBack(MainActivity.this)
                .build();

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerprintAuthenticatedCharacter.show(getSupportFragmentManager());
            }
        });
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (codedLockAuthenticatedCharacter.isKeyguardSecure()) {
                    codedLockAuthenticatedCharacter.onValidate();
                }
            }
        });
    }


    @Override
    public void onFingerprintAuthenticatedSucceed(FingerprintManager.CryptoObject cryptoObject, boolean withFingerprint) {
//        character.onPurchased( /* withFingerprint */ withFingerprint, cryptoObject);//可以根据里面仿写，获取验证的字符串
    }

    /**
     * 密码验证activity跳转回传的结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodedLockCharacter.REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == RESULT_OK) {
                if (codedLockAuthenticatedCharacter.onValidate()) {
                    Log.e("yyy", "onActivityResult");
                } else {

                }
            } else {
                //用户取消或没有完成锁定屏幕
                Toast.makeText(this, "用户取消或没有完成锁定屏幕", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @param passWord
     * @param passWordCallback
     */
    @Override
    public void onFingerprintAuthenticatedSucceed(String passWord, InputPassWordCallback passWordCallback) {
        if ("1234".equals(passWord)) {
            if (null != passWordCallback) {
                passWordCallback.onInputSucceed();
            }
        } else {
            if (null != passWordCallback) {
                passWordCallback.onInputFailed();
            }
        }
    }

    /**
     * 密码锁验证成功
     */
    @Override
    public void onCodedLockAuthenticationSucceed() {
        Log.e("yyy", "onAuthenticationSucceed");
    }

    /**
     * 密码锁验证失败成功
     */
    @Override
    public void onCodedLockAuthenticationFailed() {
        Log.e("yyy", "onCodedLockAuthenticationFailed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != codedLockAuthenticatedCharacter) {
            codedLockAuthenticatedCharacter.onDestroy();
        }
    }
}
