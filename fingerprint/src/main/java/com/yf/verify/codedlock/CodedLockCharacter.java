package com.yf.verify.codedlock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.yf.verify.callback.CodedLockBaseCharacter;
import com.yf.verify.callback.CodedLockAuthenticatedCallBack;
import com.yf.verify.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * @author yangfeng
 * @create 2018/11/29 09:53
 * @Describe
 */
public class CodedLockCharacter implements CodedLockBaseCharacter {
    private Activity mActivity;
    private KeyguardManager mKeyguardManager;
    private String mKeystoreAlias;
    private CodedLockAuthenticatedCallBack callBack;
    public static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;
    private static final byte[] SECRET_BYTE_ARRAY = new byte[]{1, 2, 3, 4, 5, 6};

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public KeyguardManager getKeyguardManager() {
        return mKeyguardManager;
    }

    public void setKeyguardManager(KeyguardManager mKeyguardManager) {
        this.mKeyguardManager = mKeyguardManager;
    }

    public String getKeystoreAlias() {
        return mKeystoreAlias;
    }

    public void setKeystoreAlias(String mKeystoreAlias) {
        this.mKeystoreAlias = mKeystoreAlias;
    }

    public CodedLockAuthenticatedCallBack getAuthenticationScreenCallBack() {
        return callBack;
    }

    public void setAuthenticationScreenCallBack(CodedLockAuthenticatedCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 进行验证
     *
     * @return 返回值为是否验证成功
     */
    @Override
    public boolean onValidate() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(getKeystoreAlias(), null);
            Cipher cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + File.separator + KeyProperties.BLOCK_MODE_CBC + File.separator
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // 尝试加密某些东西，只有在最后一个AUTHENTICATION_DURATION_SECONDS 秒内通过身份验证的用户才会成功。
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            cipher.doFinal(SECRET_BYTE_ARRAY);
//            // 如果用户用户设置的秒内已经通过了密码验证。
//            if (null != getCallBack()) {
//                getCallBack().onCodedLockAuthenticationSucceed();
//            }
            return true;
        } catch (UserNotAuthenticatedException e) {
            // 用户未通过身份验证，让我们使用设备凭据进行身份验证。
            showAuthenticationScreen(getActivity());
            return false;
        } catch (KeyPermanentlyInvalidatedException e) {
            //如果锁定屏幕已被禁用或在密钥被禁用后重新设置，则会发生这种情况
            //生成密钥后生成。
            //键在创建后无效。重试
            if (null != getAuthenticationScreenCallBack()) {
                getAuthenticationScreenCallBack().onCodedLockAuthenticationFailed();
            }
            return false;
        } catch (BadPaddingException | IllegalBlockSizeException | KeyStoreException |
                CertificateException | UnrecoverableKeyException | IOException
                | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            if (null != getAuthenticationScreenCallBack()) {
                getAuthenticationScreenCallBack().onCodedLockAuthenticationFailed();
            }
            return false;
        }
    }

    /**
     * 跳转到软键盘输入界面
     *
     * @param activity
     */
    @Override
    public void showAuthenticationScreen(Activity activity) {
        // 创建确认凭据屏幕。您可以自定义标题和描述。或 todo 可优化，让其调到手机的密码验证界面
        //如果您让它为空，我们将为您提供一个通用的
        //避免过多显示重新验证对话框 -- 您的应用应尝试先使用加密对象，如果超时到期，请使用 createConfirmDeviceCredentialIntent() 方法在您的应用内重新验证用户身份。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
            if (null != activity && null != intent) {
                activity.startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
            } else {
                if (null != getAuthenticationScreenCallBack()) {
                    getAuthenticationScreenCallBack().onCodedLockAuthenticationFailed();
                }
            }
        } else {
            if (null != getAuthenticationScreenCallBack()) {
                getAuthenticationScreenCallBack().onCodedLockAuthenticationFailed();
            }
        }
    }

    @Override
    public void onDestroy() {
        mKeyguardManager = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CodedLockCharacter.REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == Activity.RESULT_OK) {
                if (onValidate()) {
                    getAuthenticationScreenCallBack().onCodedLockAuthenticationSucceed();
                } else {
                    getAuthenticationScreenCallBack().onCodedLockAuthenticationFailed();
                }
            } else {
                getAuthenticationScreenCallBack().onCodedLockAuthenticationCancel();
            }
        }
    }

    /**
     * 是否有密码锁
     *
     * @return
     */
    @Override
    public boolean isKeyguardSecure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (null != getKeyguardManager() && getKeyguardManager().isKeyguardSecure()) {
                return true;
            }
        }
        return false;
    }


}
