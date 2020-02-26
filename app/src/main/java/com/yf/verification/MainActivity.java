package com.yf.verification;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yf.verify.callback.FingerprintAuthenticatedCallback;
import com.yf.verify.callback.CodedLockAuthenticatedCallBack;
import com.yf.verify.codedlock.CodedLockCharacter;
import com.yf.verify.codedlock.CodedLockAuthenticatedStepBuilder;
import com.yf.verify.fingerprint.FingerprintCharacter;
import com.yf.verify.fingerprint.FingerprintCharacterStepBuilder;

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
     * 密码验证activity跳转回传的结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        codedLockAuthenticatedCharacter.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 指纹验证成功
     */
    @Override
    public void onFingerprintSucceed() {
        Toast.makeText(this, "指纹验证成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFingerprintFailed() {

    }

    @Override
    public void onFingerprintCancel() {

    }

    @Override
    public void noEnrolledFingerprints() {
        Toast.makeText(this, "没有录入指纹锁", Toast.LENGTH_SHORT).show();
    }

    /**
     * 密码锁验证失败成功
     */
    @Override
    public void onCodedLockAuthenticationFailed() {
        Toast.makeText(this, "密码验证失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCodedLockAuthenticationSucceed() {
        Toast.makeText(this, "密码验证成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCodedLockAuthenticationCancel() {
        Toast.makeText(this, "密码验证取消", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != codedLockAuthenticatedCharacter) {
            codedLockAuthenticatedCharacter.onDestroy();
        }
    }
}
