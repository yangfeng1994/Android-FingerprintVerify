/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.yf.verify.fingerprint;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

import com.yf.verify.R;
import com.yf.verify.callback.FingerprintAuthenticatedCallback;


/**
 * 用来管理指纹验证UI的文本/图标的帮助类。
 */

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintUiHelper extends FingerprintManagerCompat.AuthenticationCallback {

    private static final long ERROR_TIMEOUT_MILLIS = 1600;
    private static final long SUCCESS_DELAY_MILLIS = 1300;

    private final FingerprintManagerCompat mFingerprintManager;
    private final ImageView mIcon;
    private final TextView mErrorTextView;
    private final FingerprintAuthenticatedCallback mCallback;
    private CancellationSignal mCancellationSignal;

    private boolean mSelfCancelled;

    /**
     * Constructor for {@link FingerprintUiHelper}.
     */
    FingerprintUiHelper(
            ImageView icon, TextView errorTextView, FingerprintManagerCompat fingerprintManager, FingerprintAuthenticatedCallback callback) {
        this.mFingerprintManager = fingerprintManager;
        mIcon = icon;
        mErrorTextView = errorTextView;
        mCallback = callback;
    }


    /**
     * 开启指纹验证
     *
     * @param cryptoObject
     */
    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        mFingerprintManager
                .authenticate(cryptoObject, 0, mCancellationSignal, this, null);
        mIcon.setImageResource(R.drawable.icon_fingerprint);
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!mSelfCancelled) {
            showError(errString);
            mIcon.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (null != mCallback) {
                        mCallback.onFingerprintFailed();
                    }
                }
            }, ERROR_TIMEOUT_MILLIS);
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
//        showError(helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        showError("验证失败，请重新验证");
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mIcon.setImageResource(R.drawable.ic_fingerprint_success);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.success_color, null));
        mErrorTextView.setText("验证成功");
        mIcon.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mCallback) {
                    mCallback.onFingerprintSucceed();
                }
            }
        }, SUCCESS_DELAY_MILLIS);
    }

    private void showError(CharSequence error) {
        mIcon.setImageResource(R.drawable.ic_fingerprint_error);
        mErrorTextView.setText(error);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.warning_color, null));
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mErrorTextView.postDelayed(mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }

    private Runnable mResetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            mErrorTextView.setTextColor(
                    mErrorTextView.getResources().getColor(R.color.hint_color, null));
            mErrorTextView.setText("请触摸指纹进行验证");
            mIcon.setImageResource(R.drawable.icon_fingerprint);
        }
    };

}
