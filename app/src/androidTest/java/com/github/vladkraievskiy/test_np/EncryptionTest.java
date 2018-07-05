package com.github.vladkraievskiy.test_np;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.vladkraievskiy.test_np.data.local.SPHelper;
import com.github.vladkraievskiy.test_np.encryption.EncryptedData;
import com.github.vladkraievskiy.test_np.encryption.Encryption;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.KeyStore;
import java.security.KeyStoreException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class EncryptionTest {

    private Context appContext;
    private SPHelper spHelper;
    private KeyStore androidKeyStore;

    @Before
    public void before() throws Exception {
        appContext = InstrumentationRegistry.getTargetContext();
        spHelper = new SPHelper(appContext, "test_shared");
        androidKeyStore = KeyStore.getInstance("AndroidKeyStore");
        androidKeyStore.load(null);

        spHelper.setAesKey("", "");
        androidKeyStore.deleteEntry("RSA alias");
    }

    @After
    public void after() throws KeyStoreException {
        spHelper.setAesKey("", "");
        androidKeyStore.deleteEntry("RSA alias");
    }

    @Test
    public void testingNormalFlow() {
        for (int i = 0; i < 10; i++) {
            Encryption encryption = new Encryption(spHelper);
            encryption.tryGenerateKeys(appContext, false);

            EncryptedData encrypt = encryption.encrypt("test".getBytes());
            byte[] decrypt = encryption.decrypt(encrypt);
            assertNotNull("Iteration - " + i, decrypt);
            assertEquals("test", new String(decrypt));
        }
    }
}
