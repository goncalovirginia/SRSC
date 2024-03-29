package _1PBEncryption.PBE_Exercises;// Neste caso usa-se PBE sem parametros

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Examplo de uso do esquema PBE sem usar parametros
 */
public class PBEWithoutParamsExample {
	public static void main(
			String[] args)
			throws Exception {
		byte[] input = new byte[]{
				0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
				0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
				0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
		byte[] keyBytes = new byte[]{
				0x73, 0x2f, 0x2d, 0x33, (byte) 0xc8, 0x01, 0x73,
				0x2b, 0x72, 0x06, 0x75, 0x6c, (byte) 0xbd, 0x44,
				(byte) 0xf9, (byte) 0xc1, (byte) 0xc1, 0x03, (byte) 0xdd,
				(byte) 0xd9, 0x7c, 0x7c, (byte) 0xbe, (byte) 0x8e};
		byte[] ivBytes = new byte[]{
				(byte) 0xb0, 0x7b, (byte) 0xf5, 0x22, (byte) 0xc8,
				(byte) 0xd6, 0x08, (byte) 0xb8};


		System.out.println("plaintext : " + Utils.toHex(input));

		// Cifrar com chaves pre-calculadas

		Cipher cEnc = Cipher.getInstance("DESede/CBC/PKCS7Padding", "BC");


		cEnc.init(Cipher.ENCRYPT_MODE,
				new SecretKeySpec(keyBytes, "DESede"),
				new IvParameterSpec(ivBytes));

		byte[] out = cEnc.doFinal(input);

		// Decifrar

		char[] password = "password".toCharArray();
		byte[] salt = new byte[]{0x7d, 0x60, 0x43, 0x5f, 0x02, (byte) 0xe9, (byte) 0xe0, (byte) 0xae};
		int iterationCount = 2048;

		// Misturar ja o salt e o iterador a usar na geracap da chave

		PBEKeySpec pbeSpec = new PBEKeySpec(password, salt, iterationCount);
		SecretKeyFactory keyFact = SecretKeyFactory.getInstance("PBEWithSHAAnd3KeyTripleDES");

		Cipher cDec = Cipher.getInstance("PBEWithSHAAnd3KeyTripleDES");
		Key sKey = keyFact.generateSecret(pbeSpec);

		// Decifrar ja com a chave gerada com base no esquema PBE

		cDec.init(Cipher.DECRYPT_MODE, sKey);

		System.out.println("cipher : " + Utils.toHex(out));
		System.out.println("gen key: " + Utils.toHex(sKey.getEncoded()));
		System.out.println("gen iv : " + Utils.toHex(cDec.getIV()));
		System.out.println("plain  : " + Utils.toHex(cDec.doFinal(out)));
	}
}
