package com.hpe.sb.mobile.app.infra.encryption;

/**
 * Service from encrypting\decrypting strings. Encryption key is managed internally.
 */
public interface EncryptionService {

    String encrypt(String toEncrypt);

    String decrypt(String toDecrypt);

}
