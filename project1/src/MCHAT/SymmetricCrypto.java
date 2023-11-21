package MCHAT;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class SymmetricCrypto {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static byte[] encrypt(byte[] content) throws CryptoException {
		return crypto(Cipher.ENCRYPT_MODE, SecurityConfig.SYMMETRIC_KEY, content);
	}

	public static byte[] decrypt(byte[] content) throws CryptoException {
		return crypto(Cipher.DECRYPT_MODE, SecurityConfig.SYMMETRIC_KEY, content);
	}

	private static byte[] crypto(int mode, byte[] key, byte[] content) throws CryptoException {
		try {
			return switch (SecurityConfig.SYMMETRIC_ALGORITHM) {
				case "RC4" -> streamCrypto(mode, key, content);
				case "CHACHA20" -> chacha20Crypto(mode, key, content);
				default -> blockCrypto(mode, key, content);
			};
		} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
				 IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			throw new CryptoException(e.getMessage());
		}
	}

	private static byte[] blockCrypto(int mode, byte[] key, byte[] content) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		IvParameterSpec ivSpec = new IvParameterSpec(SecurityConfig.IV);
		Key secretKey = new SecretKeySpec(key, SecurityConfig.SYMMETRIC_ALGORITHM.split("/")[0]);
		Cipher cipher = Cipher.getInstance(SecurityConfig.SYMMETRIC_ALGORITHM);
		cipher.init(mode, secretKey, ivSpec);
		return cipher.doFinal(content);
	}

	private static byte[] streamCrypto(int mode, byte[] key, byte[] content) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Key secretKey = new SecretKeySpec(key, SecurityConfig.SYMMETRIC_ALGORITHM.split("/")[0]);
		Cipher cipher = Cipher.getInstance(SecurityConfig.SYMMETRIC_ALGORITHM);
		cipher.init(mode, secretKey);
		return cipher.doFinal(content);
	}

	private static byte[] chacha20Crypto(int mode, byte[] key, byte[] content) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		ChaCha20ParameterSpec ivSpec = new ChaCha20ParameterSpec(SecurityConfig.IV, 1);
		Key secretKey = new SecretKeySpec(key, SecurityConfig.SYMMETRIC_ALGORITHM.split("/")[0]);
		Cipher cipher = Cipher.getInstance(SecurityConfig.SYMMETRIC_ALGORITHM);
		cipher.init(mode, secretKey, ivSpec);
		return cipher.doFinal(content);
	}

}
