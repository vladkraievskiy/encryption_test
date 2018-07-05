package com.github.vladkraievskiy.test_np.data.repository;

import android.support.annotation.NonNull;

import com.github.vladkraievskiy.test_np.encryption.EncryptedData;

public interface Repository {

    void getRepositories(@NonNull final GetRepositoryCallback callback);

    interface GetRepositoryCallback {
        void onResult(@NonNull final EncryptedData result);
    }
}
