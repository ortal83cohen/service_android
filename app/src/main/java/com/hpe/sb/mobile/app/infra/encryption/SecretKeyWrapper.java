/*
 * Haim: This code is based on:
 *
 * https://android.googlesource.com/platform/development/+/master/samples/Vault/src/com/example/android/vault/SecretKeyWrapper.java
 *
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hpe.sb.mobile.app.infra.encryption;

import android.annotation.SuppressLint;
import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Log;

import com.hpe.sb.mobile.app.infra.exception.EncryptionInitException;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

/**
 * Wraps {@link SecretKey} instances using a public/private keyPair pair stored in
 * the platform {@link KeyStore}. This allows us to protect symmetric keys with
 * hardware-backed crypto, if provided by the device.
 * <p/>
 * See <a href="http://en.wikipedia.org/wiki/Key_Wrap">keyPair wrapping</a> for more
 * details.
 * <p/>
 * Not inherently thread safe.
 */
public class SecretKeyWrapper {

    public static final String ALIAS = "SecretKeyWrapper";
    public static final String ENCRYPTION_ALGORITHM = "RSA/ECB/PKCS1Padding";

    private KeyPair keyPair = null;
    private Cipher cipher;

    @SuppressLint("GetInstance") // Fixing lint requires API 23+
    public void init(Context context) {
        try {
            cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            if (!keyStore.containsAlias(ALIAS)) {
                createEncryptionKey(context);
            }

            // Even if we just generated the keyPair, always read it back to ensure we
            // can read it successfully.
            final KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(
                    ALIAS, null);
            keyPair = new KeyPair(entry.getCertificate().getPublicKey(), entry.getPrivateKey());
        } catch (InvalidAlgorithmParameterException | NoSuchProviderException |
                UnrecoverableEntryException | NoSuchPaddingException | IOException |
                NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            Log.e(LogTagConstants.ENCRYPTION_SERVICE, "Unable to load keystore or alias", e);
            throw new EncryptionInitException(e);
        }
    }

    private void createEncryptionKey(Context context) throws NoSuchPaddingException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
            UnrecoverableEntryException, KeyStoreException {
        final Calendar start = new GregorianCalendar();
        final Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 100);

        final KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                .setAlias(ALIAS)
                .setSubject(new X500Principal("CN=" + ALIAS))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();
        final KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
        gen.initialize(spec);
        gen.generateKeyPair();
    }

    /**
     * Wrap a {@link SecretKey} using the public keyPair assigned to this wrapper.
     * Use {@link #unwrap(byte[])} to later recover the original
     * {@link SecretKey}.
     *
     * @return a wrapped version of the given {@link SecretKey} that can be
     * safely stored on untrusted storage.
     */
    public byte[] wrap(SecretKey key) {
        try {
            cipher.init(Cipher.WRAP_MODE, keyPair.getPublic());
            return cipher.wrap(key);
        } catch (IllegalBlockSizeException | InvalidKeyException e) {
            throw new EncryptionInitException(e);
        }
    }

    /**
     * Unwrap a {@link SecretKey} using the private keyPair assigned to this
     * wrapper.
     *
     * @param blob a wrapped {@link SecretKey} as previously returned by
     *             {@link #wrap(SecretKey)}.
     */
    public SecretKey unwrap(byte[] blob, String algorithm) {
        try {
            cipher.init(Cipher.UNWRAP_MODE, keyPair.getPrivate());
            return (SecretKey) cipher.unwrap(blob, algorithm, Cipher.SECRET_KEY);
        } catch (GeneralSecurityException e) {
            throw new EncryptionInitException(e);
        }
    }

}
