package com.github.vladkraievskiy.test_np.encryption;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Base64;
import android.util.Log;

import com.github.vladkraievskiy.test_np.BuildConfig;
import com.github.vladkraievskiy.test_np.data.local.SPHelper;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Calendar;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

public final class Encryption {

    private static final String TAG = Encryption.class.getSimpleName();
    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "AES";

    private static final String PROVIDER_TYPE;
    private static final String RSA_ALIAS;

    private static final int RSA_KEY_SIZE;

    private static final int AES_KEY_SIZE;
    private static final int IV_SIZE;
    private static final int GCM_TAG_LENGTH;

    private static final String RSA_CIPHER;
    private static final String AES_CIPHER;

    private static final String SIGNATURE_ALGORITHM;

    static {
        System.loadLibrary(BuildConfig.NATIVE_LIBRARY_NAME);

        RSA_ALIAS = getRsaAlias();
        PROVIDER_TYPE = getProviderType();
        RSA_KEY_SIZE = getRsaKeySize();
        AES_KEY_SIZE = getAesKeySize();
        IV_SIZE = getIvSize();
        GCM_TAG_LENGTH = getGcmTagLength();
        RSA_CIPHER = getRsaCipher();
        AES_CIPHER = getAesCipher();
        SIGNATURE_ALGORITHM = getSignatureAlgorithm();
    }

    private final SPHelper spHelper;
    private KeyStore keyStore;

    public Encryption(@NonNull SPHelper spHelper) {
        this.spHelper = spHelper;

        try {
            keyStore = KeyStore.getInstance(PROVIDER_TYPE);
            keyStore.load(null);
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Unable to create keystore.", e);
        }
    }

    @NonNull
    public EncryptedData encrypt(@NonNull final byte[] data) {
        try {
            Cipher cipher = getAESCipher(Cipher.ENCRYPT_MODE);

            String encryptedData = Base64.encodeToString(cipher.doFinal(data), Base64.NO_WRAP);
            String signature = sign(data);
            return new EncryptedData(encryptedData, signature);
        } catch (GeneralSecurityException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return new EncryptedData();
    }

    @NonNull
    public byte[] decrypt(@NonNull final EncryptedData data) {
        try {
            Cipher cipher = getAESCipher(Cipher.DECRYPT_MODE);

            byte[] decryptedBytes = cipher.doFinal(Base64.decode(data.getData(), Base64.NO_WRAP));
            if (!verifySignature(data.getSignature(), decryptedBytes)) {
                return new byte[0];
            }

            return decryptedBytes;
        } catch (GeneralSecurityException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return new byte[0];
    }

    @NonNull
    private String sign(@NonNull final byte[] data) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(RSA_ALIAS, null);
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();
            signature.initSign(privateKey);

            signature.update(data);

            return Base64.encodeToString(signature.sign(), Base64.NO_WRAP);
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "Error occurred while signing.", e);
            return "";
        }
    }

    private boolean verifySignature(@NonNull final String signatureData, byte[] decryptedBytes) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(keyStore.getCertificate(RSA_ALIAS));
            signature.update(decryptedBytes);

            return signature.verify(Base64.decode(signatureData, Base64.NO_WRAP));
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "Error while verifying signature.", e);
            return false;
        }
    }

    private Cipher getAESCipher(final int mode) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(AES_CIPHER);
        cipher.init(mode, new SecretKeySpec(getAesKey(), AES_ALGORITHM), new GCMParameterSpec(GCM_TAG_LENGTH, getIV()));

        return cipher;
    }

    public void tryGenerateKeys(@NonNull final Context context) {
        tryGenerateKeys(context, true);
    }

    @VisibleForTesting
    public void tryGenerateKeys(@NonNull final Context context, boolean encrypted) {
        tryGenerateRSAKeys(context, encrypted);
        tryGenerateAESKey();
    }

    private void tryGenerateRSAKeys(@NonNull final Context context, boolean encrypted) {
        if (containsKey(RSA_ALIAS) || keyStore == null) {
            return;
        }

        try {
            generateRSAKeys(context, encrypted);
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "Error generating keys", e);
        }
    }

    private void generateRSAKeys(final Context context, boolean encrypted) throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_ALGORITHM, keyStore.getProvider());

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 25);

        KeyPairGeneratorSpec.Builder spec = new KeyPairGeneratorSpec.Builder(context);

        if (encrypted) {
            spec.setEncryptionRequired();
        }

        generator.initialize(
                spec.setKeySize(RSA_KEY_SIZE)
                        .setAlias(RSA_ALIAS)
                        .setSubject(new X500Principal("CN=Vlad"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build()
        );

        generator.generateKeyPair();
    }

    private void tryGenerateAESKey() {
        if (!spHelper.getAesKey().isEmpty()) {
            return;
        }

        try {
            generateAESKey();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private byte[] getAesKey() throws GeneralSecurityException {
        return getRSACipherForDecrypt().doFinal(Base64.decode(spHelper.getAesKey(), Base64.NO_WRAP));
    }

    private byte[] getIV() throws GeneralSecurityException {
        return getRSACipherForDecrypt().doFinal(Base64.decode(spHelper.getIvKey(), Base64.NO_WRAP));
    }

    private Cipher getRSACipherForDecrypt() throws GeneralSecurityException {
        Cipher instance = Cipher.getInstance(RSA_CIPHER);
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(RSA_ALIAS, null);
        instance.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

        return instance;
    }

    private void generateAESKey() throws GeneralSecurityException {
        KeyGenerator generator = KeyGenerator.getInstance(AES_ALGORITHM);
        generator.init(AES_KEY_SIZE);

        SecretKey secretKey = generator.generateKey();

        Cipher cipher = Cipher.getInstance(RSA_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, keyStore.getCertificate(RSA_ALIAS));

        byte[] aesEncrypted = cipher.doFinal(secretKey.getEncoded());
        byte[] iv = new byte[IV_SIZE];
        new Random().nextBytes(iv);
        byte[] ivEncrypted = cipher.doFinal(iv);

        spHelper.setAesKey(
                Base64.encodeToString(aesEncrypted, Base64.NO_WRAP),
                Base64.encodeToString(ivEncrypted, Base64.NO_WRAP)
        );
    }

    private boolean containsKey(@NonNull final String alias) {
        if (keyStore == null) {
            return false;
        }

        try {
            return keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            return false;
        }
    }

    private static native String getRsaAlias();

    private static native String getProviderType();

    private static native int getRsaKeySize();

    private static native int getAesKeySize();

    private static native int getIvSize();

    private static native int getGcmTagLength();

    private static native String getRsaCipher();

    private static native String getAesCipher();

    private static native String getSignatureAlgorithm();
}
