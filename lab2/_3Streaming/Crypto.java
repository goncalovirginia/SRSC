package _3Streaming;

import encryptionDecryption.ex3.CryptoException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class Crypto {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CTR/NoPadding";
    private static final String KEY = "keykeykeykeykeyk";

    private static final byte[] ivBytes = new byte[]{
            0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00,
            0x0f, 0x0d, 0x0e, 0x0c, 0x0b, 0x0a, 0x09, 0x08
    };

    public static byte[] encrypt(byte[] content) throws CryptoException {
        return crypto(Cipher.ENCRYPT_MODE, KEY, content);
    }

    public static byte[] decrypt(byte[] content) throws CryptoException {
        return crypto(Cipher.DECRYPT_MODE, KEY, content);
    }

    private static byte[] crypto(int mode, String key, byte[] content) throws CryptoException {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(mode, secretKey, ivSpec);
            return cipher.doFinal(content);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
                 | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            throw new CryptoException("Error encrypting/decrypting content", e);
        }
    }


}
