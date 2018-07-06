package com.github.vladkraievskiy.test_np.ui;

import android.content.Context;
import android.support.annotation.NonNull;

public interface MainActivityPresenter {

    void tryToGenerateKeys(@NonNull final Context context);

    void loadRepositories();

    void onUserAuthorized();

    void detachView();
}
