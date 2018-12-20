package com.yf.verify.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yf.verify.R;
import com.yf.verify.callback.FingerprintAuthenticatedCallback;
import com.yf.verify.callback.InputPassWordCallback;
import com.yf.verify.callback.UpdateFingerprintCallback;

/**
 * @author yangfeng
 * @create 2018/11/23 14:18
 * @Describe
 */
public class FingerprintAuthenticationDialogFragment extends DialogFragment implements TextView.OnEditorActionListener, FingerprintUiHelper.Callback {
    private Button mCancelButton;
    private Button mSecondDialogButton;
    private View mFingerprintContent;
    private View mBackupContent;
    private EditText mPassword;
    private CheckBox mUseFingerprintFutureCheckBox;
    private TextView mPasswordDescriptionTextView;
    private TextView mNewFingerprintEnrolledTextView;

    private Stage mStage = Stage.FINGERPRINT;

    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;
    private UpdateFingerprintCallback mUpdateFingerprintCallback;
    private InputMethodManager mInputMethodManager;
    private FingerprintAuthenticatedCallback mFingerprintAuthenticatedCallback;

    public static FingerprintAuthenticationDialogFragment newInstance() {

        Bundle args = new Bundle();

        FingerprintAuthenticationDialogFragment fragment = new FingerprintAuthenticationDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mInputMethodManager = context.getSystemService(InputMethodManager.class);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 不要在重新创建活动时创建新的片段，例如方向更改。
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        mCancelButton = v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mSecondDialogButton = v.findViewById(R.id.second_dialog_button);
        mSecondDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStage == Stage.FINGERPRINT) {
                    goToBackup();
                } else {
                    verifyPassword();
                }
            }
        });
        mFingerprintContent = v.findViewById(R.id.fingerprint_container);
        mBackupContent = v.findViewById(R.id.backup_container);
        mPassword =  v.findViewById(R.id.password);
        mPassword.setOnEditorActionListener(this);
        mPasswordDescriptionTextView = v.findViewById(R.id.password_description);
        mUseFingerprintFutureCheckBox =
                v.findViewById(R.id.use_fingerprint_in_future_check);
        mNewFingerprintEnrolledTextView =
                v.findViewById(R.id.new_fingerprint_enrolled_description);

        mFingerprintUiHelper = new FingerprintUiHelper(getActivity(),
                (ImageView) v.findViewById(R.id.fingerprint_icon),
                (TextView) v.findViewById(R.id.fingerprint_status), this);
        updateStage();

        // 如果无法进行指纹验证，请立即切换到密码输入界面。
        if (!mFingerprintUiHelper.isFingerprintAuthAvailable()) {
            goToBackup();
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mStage == Stage.FINGERPRINT) {
            mFingerprintUiHelper.startListening(mCryptoObject);
        }
    }

    public void setStage(Stage stage) {
        mStage = stage;
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }


    /**
     * 设置使用指纹验证时要传入的crypto对象。
     */
    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        mCryptoObject = cryptoObject;
    }

    public void setUpdateFingerprintCallback(UpdateFingerprintCallback updateFingerprintCallback) {
        this.mUpdateFingerprintCallback = updateFingerprintCallback;
    }

    public void setCallback(FingerprintAuthenticatedCallback mFingerprintAuthenticatedCallback) {
        this.mFingerprintAuthenticatedCallback = mFingerprintAuthenticatedCallback;
    }

    /**
     * 切换到输入密码的界面。这两种情况都可能在指纹不存在的情况下发生
     * 可用或用户选择使用密码验证方法按下
     * 按钮。这也可能发生在用户尝试太多指纹的时候。
     */
    private void goToBackup() {
        mStage = Stage.PASSWORD;
        updateStage();
        mPassword.requestFocus();

        // 显示软件盘
        mPassword.postDelayed(mShowKeyboardRunnable, 500);
        // 指纹解锁的监听，停掉，不使用。
        mFingerprintUiHelper.stopListening();
    }

    /**
     * Checks whether the current entered password is correct, and dismisses the the dialog and
     * let's the activity know about the result.
     */
    private void verifyPassword() {
        String mPasswordStr = mPassword.getText().toString();
        if (checkPassword(mPasswordStr)) {
            Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (null != mFingerprintAuthenticatedCallback) {
            mFingerprintAuthenticatedCallback.onFingerprintAuthenticatedSucceed(mPasswordStr, new PassWordAuthenticationListener());
        }
    }

    /**
     * @return 判断密码是否输入。
     */
    private boolean checkPassword(String password) {
        return TextUtils.isEmpty(password);
    }

    private final Runnable mShowKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != mInputMethodManager) {
                mInputMethodManager.showSoftInput(mPassword, 0);
            }
        }
    };

    private void updateStage() {
        switch (mStage) {
            case FINGERPRINT:
                mCancelButton.setText("取消");
                mSecondDialogButton.setText("使用密码");
                mFingerprintContent.setVisibility(View.VISIBLE);
                mBackupContent.setVisibility(View.GONE);
                break;
            case NEW_FINGERPRINT_ENROLLED:
                // 当新录入一个指纹的时候。
            case PASSWORD:
                mCancelButton.setText("取消");
                mSecondDialogButton.setText("确定");
                mFingerprintContent.setVisibility(View.GONE);
                mBackupContent.setVisibility(View.VISIBLE);
                if (mStage == Stage.NEW_FINGERPRINT_ENROLLED) {
                    mPasswordDescriptionTextView.setVisibility(View.GONE);
                    mNewFingerprintEnrolledTextView.setVisibility(View.VISIBLE);
                    mUseFingerprintFutureCheckBox.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            verifyPassword();
            return true;
        }
        return false;
    }

    @Override
    public void onAuthenticated() {
        // 从FingerprintUiHelper回调。让活动知道身份验证是成功的,然后把成功的回调传个调用者
        if (null != mFingerprintAuthenticatedCallback) {
            mFingerprintAuthenticatedCallback.onFingerprintAuthenticatedSucceed(mCryptoObject, true);
        }
        dismiss();
    }

    @Override
    public void onError() {
        goToBackup();
    }

    /**
     * 枚举用来指示用户试图使用哪种方式进行身份验证。
     */
    public enum Stage {
        FINGERPRINT, //指纹验证
        NEW_FINGERPRINT_ENROLLED, //新添加了指纹，需要密码验证。
        PASSWORD //密码验证
    }

    /**
     * 密码验证的回调类
     */
    public class PassWordAuthenticationListener implements InputPassWordCallback {

        @Override
        public void onInputSucceed() {
            if (mStage == Stage.NEW_FINGERPRINT_ENROLLED) {
                if (mUseFingerprintFutureCheckBox.isChecked()) {
                    // 重新创建密钥后，再次输入密码，以便验证包含新密钥的指纹。
                    if (null != mUpdateFingerprintCallback) {
                        mUpdateFingerprintCallback.onUpdateFingerprintSucceed();
                    }
                    mStage = Stage.FINGERPRINT;
                }
            }
            mPassword.setText("");
            dismiss();
        }

        @Override
        public void onInputFailed() {
            Toast.makeText(getActivity(), "密码错误,请重新输入", Toast.LENGTH_SHORT).show();
        }
    }

}
