package com.github.vladkraievskiy.test_np.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.github.vladkraievskiy.test_np.encryption.EncryptedData;

public final class SPHelper {

    private static final String PREFERENCES_NAME = "NP preferences";
    private static final String AES_KEY = "aes key";
    private static final String IV_KEY = "iv key";
    private static final String DATA_KEY = "data";

    private final SharedPreferences preferences;

    public SPHelper(@NonNull final Context context) {
        this(context, PREFERENCES_NAME);
    }

    @VisibleForTesting
    public SPHelper(@NonNull final Context context, @NonNull final String name) {
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void setAesKey(@NonNull final String aesKey, @NonNull final String iv) {
        preferences.edit().putString(AES_KEY, aesKey).putString(IV_KEY, iv).apply();
    }

    @NonNull
    public String getAesKey() {
        return preferences.getString(AES_KEY, "");
    }

    @NonNull
    public String getIvKey() {
        return preferences.getString(IV_KEY, "");
    }

    @NonNull
    public EncryptedData getData() {
        String data = preferences.getString(DATA_KEY, "");
        return EncryptedData.fromString(data);
    }

    public void saveData(@NonNull final EncryptedData data) {
        preferences.edit().putString(DATA_KEY, data.toString()).apply();
    }


}
