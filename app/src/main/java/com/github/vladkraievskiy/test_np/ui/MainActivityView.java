package com.github.vladkraievskiy.test_np.ui;

import android.support.annotation.NonNull;

public interface MainActivityView {

    void showProgress();

    void hideProgress();

    void setCipherText(@NonNull final String cipherText);

    void setText(@NonNull final String text);

    void showUnSecureDeviceDialog();
}
