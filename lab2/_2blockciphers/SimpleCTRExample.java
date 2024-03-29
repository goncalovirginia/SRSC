package _2blockciphers;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Cifra simetrica em modo CTR
 * (Modo normalizado pelo NIST, SP-800-38a e definido no RFC 3686
 */
public class SimpleCTRExample {
	public static void main(
			String[] args)
			throws Exception {
		byte[] input = new byte[]{
				0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
				0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
				0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06};

		byte[] keyBytes = new byte[]{
				0x01, 0x23, 0x45, 0x67, 0x09, 0x0a, 0x5c, (byte) 0xef,
				0x01, 0x23, 0x45, 0x67, (byte) 0x89, 0x07, 0x0d, (byte) 0xcf
		};

		byte[] ivBytes = new byte[]{
				0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00, 0x01,
				0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00, 0x01
		};

		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
		System.out.println("key   : " + Utils.toHex(keyBytes));

		System.out.println();
		System.out.println("input : " + Utils.toHex(input));

		// Cifrar
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
		int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
		ctLength += cipher.doFinal(cipherText, ctLength);
		System.out.println("cipher: " + Utils.toHex(cipherText, ctLength) + " bytes: " + ctLength);

		// Decifrar
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		byte[] plainText = new byte[cipher.getOutputSize(ctLength)];
		int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
		ptLength += cipher.doFinal(plainText, ptLength);
		System.out.println("plain : " + Utils.toHex(plainText, ptLength) + " bytes: " + ptLength);
	}
}
