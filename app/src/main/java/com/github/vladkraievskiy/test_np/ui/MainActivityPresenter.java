package com.github.vladkraievskiy.test_np.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface MainActivityPresenter {

    void tryToGenerateKeys(@NonNull final Context context, @Nullable final Runnable onSuccessTask);

    void loadRepositories();

    void detachView();
}
