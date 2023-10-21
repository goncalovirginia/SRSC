package MCHAT;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.HexFormat;

public class SymmetricCrypto {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final byte[] ivBytes = new byte[]{
            0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00,
            0x0f, 0x0d, 0x0e, 0x0c, 0x0b, 0x0a, 0x09, 0x08
    };

    public static byte[] encrypt(byte[] content) throws CryptoException {
        return crypto(Cipher.ENCRYPT_MODE, SecurityConfig.SYMMETRIC_KEY, content);
    }

    public static byte[] decrypt(byte[] content) throws CryptoException {
        return crypto(Cipher.DECRYPT_MODE, SecurityConfig.SYMMETRIC_KEY, content);
    }

    private static byte[] crypto(int mode, String key, byte[] content) throws CryptoException {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            Key secretKey = new SecretKeySpec(HexFormat.of().parseHex(key), SecurityConfig.SYMMETRIC_ALGORITHM.split("/")[0]);
            Cipher cipher = Cipher.getInstance(SecurityConfig.SYMMETRIC_ALGORITHM);
            cipher.init(mode, secretKey, ivSpec);
            return cipher.doFinal(content);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
                 | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new CryptoException(e.getMessage());
        }
    }


}
