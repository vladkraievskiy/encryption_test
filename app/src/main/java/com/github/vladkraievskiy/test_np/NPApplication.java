package com.github.vladkraievskiy.test_np;

import android.app.Application;
import android.content.Context;

import com.github.vladkraievskiy.test_np.data.local.SPHelper;
import com.github.vladkraievskiy.test_np.data.network.NetworkService;
import com.github.vladkraievskiy.test_np.data.repository.Repository;
import com.github.vladkraievskiy.test_np.data.repository.RepositoryImpl;
import com.github.vladkraievskiy.test_np.encryption.Encryption;

public final class NPApplication extends Application {

    private Encryption encryption;
    private Repository repository;
    private PresentersFactory factory;

    public static NPApplication get(Context context) {
        return (NPApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        factory = new PresentersFactory(this);
        SPHelper sharedHelper = new SPHelper(this);
        encryption = new Encryption(sharedHelper);
        NetworkService networkService = new NetworkService();
        repository = new RepositoryImpl(sharedHelper, networkService, encryption);
    }

    public PresentersFactory getPresentersFactory() {
        return factory;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    public Repository getRepository() {
        return repository;
    }
}
