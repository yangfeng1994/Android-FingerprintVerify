package com.yf.verification;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
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
import com.yf.verify.util.LogUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class MainActivity extends AppCompatActivity implements FingerprintAuthenticatedCallback, CodedLockAuthenticatedCallBack {
    private FingerprintCharacter fingerprintAuthenticatedCharacter;
    private CodedLockCharacter codedLockAuthenticatedCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button purchaseButton = findViewById(R.id.purchase_button);
        Button passwordButton = findViewById(R.id.password_button);
        //初始化指纹验证
        fingerprintAuthenticatedCharacter = FingerprintCharacterStepBuilder
                .newBuilder()
                .setKeystoreAlias("key1")
                .setDialogTag(FingerprintCharacterStepBuilder.DIALOG_FRAGMENT_TAG)
                .setFingerprintCallback(this)
                .build();

        //初始化密码验证
        codedLockAuthenticatedCharacter = CodedLockAuthenticatedStepBuilder
                .newBuilder()
                .setActivity(MainActivity.this)
                .getKeyguardManager()
                .setKeystoreAlias("my_key")
                .setUserAuthenticationValidityDurationSeconds(3)
                .getKeyStore()
                .setAuthenticationScreenCallBack(MainActivity.this)
                .build();

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerprintAuthenticatedCharacter.show(MainActivity.this);
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

    /**
     * 指纹验证成功
     */
    @Override
    public void onFingerprintAuthenticatedSucceed() {
        Toast.makeText(this, "指纹验证成功", Toast.LENGTH_SHORT).show();
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
                    LogUtils.e("yyy", "密码验证成功");
                } else {
                    LogUtils.e("yyy", "密码验证失败");
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
        if ("1234".equals(passWord)) {//成功后，调用成功的方法，在dialog中，可以让dialog关闭
            if (null != passWordCallback) {
                passWordCallback.onInputSucceed();
            }
        } else {
            if (null != passWordCallback) {//失败后，调用失败的方法，在dialog中，可以弹出toast，如果想自己定义，可以不调用次方法
                passWordCallback.onInputFailed();
            }
        }
    }

    /**
     * 密码锁验证失败成功
     */
    @Override
    public void onCodedLockAuthenticationFailed() {
        LogUtils.e("yyy", "密码验证失败");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != codedLockAuthenticatedCharacter) {
            codedLockAuthenticatedCharacter.onDestroy();
        }
    }
}
