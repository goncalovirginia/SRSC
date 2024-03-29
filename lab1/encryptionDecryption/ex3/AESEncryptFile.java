package encryptionDecryption.ex3;

import java.io.File;

/*
 *  Main class for encryption/decryption
 *  using CryptoStuff Class
 */
public class AESEncryptFile {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Use: AESEncryptFile <aes-key> <file>");
			System.out.println("You must use a correct key size ...");
			System.out.println("For AES possible keysizes: 128, 192 or 256 bits");
			System.exit(-1);
		}

		String key = args[0]; // Need AES Key, 128 or 256 bits
		// We could generate from a seed or pwd
		// Later ...
		File inputFile = new File(args[1]);
		String encryptedfile = args[1] + ".enc";
		File encryptedFile = new File(encryptedfile);

		try {
			System.out.println("Input file: " + inputFile);
			CryptoStuff.encrypt(key, inputFile, encryptedFile);
			System.out.println("Encrypted file: " + encryptedFile);
			String decryptedFileString = CryptoStuff.decrypt(key, encryptedFile);
			System.out.println("Decrypted: " + encryptedFile);
			System.out.println(decryptedFileString);
		} catch (CryptoException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}

	}

}


