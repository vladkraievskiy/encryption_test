package com.github.vladkraievskiy.test_np.ui;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.vladkraievskiy.test_np.data.repository.Repository;
import com.github.vladkraievskiy.test_np.encryption.EncryptedData;
import com.github.vladkraievskiy.test_np.encryption.Encryption;

import java.lang.ref.WeakReference;

public final class MainActivityPresenterImpl implements MainActivityPresenter {

    private final Encryption encryption;
    private final Handler mainThreadHandler;
    private final KeyguardManager keyguardManager;
    private final Repository repository;

    private final WeakReference<MainActivityView> viewWeakReference;

    public MainActivityPresenterImpl(
            @NonNull final MainActivityView view,
            @NonNull final Encryption encryption,
            @NonNull final KeyguardManager keyguardManager,
            @NonNull final Repository repository) {
        viewWeakReference = new WeakReference<>(view);
        this.encryption = encryption;
        this.keyguardManager = keyguardManager;
        this.repository = repository;
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public void tryToGenerateKeys(@NonNull final Context context, @Nullable final Runnable onSuccessTask) {
        if (!keyguardManager.isKeyguardSecure()) {
            runTaskOnUi(new Runnable() {
                @Override
                public void run() {
                    if (isViewDetached()) {
                        return;
                    }

                    viewWeakReference.get().showUnSecureDeviceDialog();
                }
            });

            return;
        }

        encryption.tryGenerateKeys(context);

        if (onSuccessTask != null) {
            onSuccessTask.run();
        }
    }

    @Override
    public void loadRepositories() {
        viewWeakReference.get().showProgress();

        repository.getRepositories(new Repository.GetRepositoryCallback() {
            @Override
            public void onResult(@NonNull final EncryptedData result) {
                runTaskOnUi(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewDetached()) {
                            return;
                        }

                        showEncryptedData(result);
                        showDecryptedData(result);
                        viewWeakReference.get().hideProgress();
                    }
                });
            }
        });
    }

    private void showDecryptedData(@NonNull final EncryptedData result) {
        viewWeakReference.get().setText(result.getDecryptedValue());
    }

    private void showEncryptedData(@NonNull final EncryptedData data) {
        viewWeakReference.get().setCipherText(data.getData());
    }

    private void runTaskOnUi(@NonNull final Runnable task) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isViewDetached()) {
                    return;
                }

                task.run();
            }
        });
    }

    private boolean isViewDetached() {
        return viewWeakReference.get() == null;
    }

    @Override
    public void detachView() {
        viewWeakReference.clear();
    }
}
