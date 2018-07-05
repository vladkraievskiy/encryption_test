package com.github.vladkraievskiy.test_np.data.repository;


import android.support.annotation.NonNull;

import com.github.vladkraievskiy.test_np.data.local.SPHelper;
import com.github.vladkraievskiy.test_np.data.network.NetworkService;
import com.github.vladkraievskiy.test_np.encryption.EncryptedData;
import com.github.vladkraievskiy.test_np.encryption.Encryption;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class RepositoryImpl implements Repository {

    private final SPHelper sharedHelper;
    private final NetworkService networkService;
    private final Executor backgroundExecutor;
    private final Encryption encryption;

    public RepositoryImpl(
            @NonNull final SPHelper sharedHelper,
            @NonNull final NetworkService networkService,
            @NonNull final Encryption encryption
    ) {
        this.sharedHelper = sharedHelper;
        this.networkService = networkService;
        this.encryption = encryption;
        backgroundExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void getRepositories(@NonNull final GetRepositoryCallback callback) {
        final EncryptedData data = sharedHelper.getData();
        if (!data.getData().isEmpty()) {
            callback.onResult(withDecryption(data));
            return;
        }

        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String repositories = networkService.getRepositories();

                EncryptedData encryptedData = encryption.encrypt(repositories.getBytes());
                sharedHelper.saveData(encryptedData);
                callback.onResult(withDecryption(encryptedData));
            }
        });
    }

    private EncryptedData withDecryption(@NonNull final EncryptedData data) {
        data.setDecryptedValue(new String(encryption.decrypt(data)));
        return data;
    }
}
