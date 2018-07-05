package com.github.vladkraievskiy.test_np.data.network;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class NetworkService {

    private static final String TAG = NetworkService.class.getSimpleName();

    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final String GITHUB_GET_REPOSITORIES_REQUEST = "/users/vladkraievskiy/repos";
    private static final int BUFFER_LENGTH = 4096;

    public String getRepositories() {
        try {
            URL url = new URL(GITHUB_API_URL + GITHUB_GET_REPOSITORIES_REQUEST);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.connect();
            try (InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                 ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
                byte[] tmp = new byte[BUFFER_LENGTH];
                while (inputStream.read(tmp) != -1) {
                    bytes.write(tmp, 0, tmp.length);
                }

                return new String(bytes.toByteArray());
            } finally {
                httpURLConnection.disconnect();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return "";
    }
}