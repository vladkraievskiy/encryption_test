package com.github.vladkraievskiy.test_np;

import android.app.KeyguardManager;
import android.content.Context;
import android.support.annotation.NonNull;

import com.github.vladkraievskiy.test_np.ui.MainActivityPresenter;
import com.github.vladkraievskiy.test_np.ui.MainActivityPresenterImpl;
import com.github.vladkraievskiy.test_np.ui.MainActivityView;

import java.util.Objects;

public final class PresentersFactory {

    private final Context context;

    PresentersFactory(Context context) {
        this.context = context;
    }

    @NonNull
    public MainActivityPresenter createMainActivityPresenter(@NonNull final MainActivityView view) {
        NPApplication application = NPApplication.get(context);
        KeyguardManager keyguardManager = (KeyguardManager) Objects.requireNonNull(context.getSystemService(Context.KEYGUARD_SERVICE));
        return new MainActivityPresenterImpl(
                view,
                application.getEncryption(),
                keyguardManager,
                application.getRepository()
        );
    }
}
