package utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.Base64;

public class KeyStores {

	public static KeyStore createOrLoadKeyStore(String filename, String password) throws Exception {
		File file = new File(filename);
		KeyStore keyStore = KeyStore.getInstance("JKS");

		if (file.exists()) {
			keyStore.load(new FileInputStream(file), password.toCharArray());
		} else {
			keyStore.load(null, null);
			keyStore.store(new FileOutputStream(filename), password.toCharArray());
		}

		return keyStore;
	}

	public static SecretKey createKeyInKeystore(String filename, String password, String keyName, String keyPassword) throws Exception {
		KeyStore keyStore = createOrLoadKeyStore(filename, password);
		SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();

		KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
		KeyStore.PasswordProtection secretKeyPassword = new KeyStore.PasswordProtection(keyPassword.toCharArray());
		keyStore.setEntry(keyName, secretKeyEntry, secretKeyPassword);
		keyStore.store(new FileOutputStream(filename), password.toCharArray());

		return secretKey;
	}

	public static SecretKey getKeyInKeystore(String filename, String password, String keyName, String keyPassword) throws Exception {
		return ((KeyStore.SecretKeyEntry) createOrLoadKeyStore(filename, password).getEntry(keyName, new KeyStore.PasswordProtection(keyPassword.toCharArray()))).getSecretKey();
	}

}
