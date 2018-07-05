package com.github.vladkraievskiy.test_np.encryption;

import android.support.annotation.NonNull;

public final class EncryptedData {

    private static final String SEPARATOR = " ";

    private final String data;
    private final String signature;
    private String decryptedValue;

    public static EncryptedData fromString(@NonNull final String data) {
        if (data.isEmpty()) {
            return new EncryptedData();
        }

        String[] parts = data.split(SEPARATOR);
        return new EncryptedData(parts[0], parts[1]);
    }

    EncryptedData() {
        this("", "");
    }

    EncryptedData(@NonNull final String data, @NonNull final String signature) {
        this.data = data;
        this.signature = signature;
    }

    public String getData() {
        return data;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return getData() + SEPARATOR + getSignature();
    }

    public String getDecryptedValue() {
        return decryptedValue;
    }

    public void setDecryptedValue(String decryptedValue) {
        this.decryptedValue = decryptedValue;
    }
}
