package encryptionDecryption.ex2.receivedecrypt;
/**
 * Material/Labs para SRSC 20/21, Sem-1
 * hj
 **/

// ReceiveDecrypt.java 
// Recebe e decifra 

import encryptionDecryption.common.KeyRing;
import encryptionDecryption.common.Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.DataInputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Decifra
 */
public class ReceiveDecrypt {
	
	public static void main(String[] args) throws Exception {
		
		int port = 5999; // Default, change it if you want
		
		// Ciphersuite security associations (parameters)
		// Crypto Alg., Mode and Padding
		String ciphersuite = "AES/CTR/NoPadding";
		
		if (args.length == 1) {
			port = Integer.parseInt(args[0]);
		}
		
		if (args.length == 2) {
			port = Integer.parseInt(args[0]);
			ciphersuite = args[1];
		}
		
		
		// ok I know/share the IV with the sender ...)
		byte[] ivBytes = new byte[]{
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
			0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15
		};
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		
		System.out.println("Hostname: " + InetAddress.getLocalHost().getHostAddress());
		System.out.println("Wait cyphertext o port: " + port);
		System.out.println("Ciphersuite: " + ciphersuite);
		System.out.println();
		
		// Ler chave secreta
		SecretKey key = KeyRing.readSecretKey(); // Ok, I know / I share the key with sender
		byte[] ciphertext;
		
		for (; ; ) {
			ServerSocket ss = new ServerSocket(port);
			
			// Receive on my TCP socket
			
			try {
				Socket s = ss.accept();
				try {
					DataInputStream is = new DataInputStream(s.getInputStream());
					ciphertext = new byte[is.readInt()];
					is.read(ciphertext);
					
					System.out.println("----------------------------------------------");
					System.out.println("Ciphertext recebido (em HEX) ...:\n"
						+ Utils.toHex(ciphertext, ciphertext.length)
						+ " Size: " + ciphertext.length);
					
				} finally {
					try {
						s.close();
					} catch (Exception e) {
						
						// Nothing by now ... Exception handler if you want
					}
				}
			} finally {
				try {
					ss.close();
				} catch (Exception e) {
					
					// Nothing by now ... Exception Handler if you want
				}
			}
			
			// Decryption
			
			System.out.println("Decrypt received ciphertext ...");
			
			Cipher cipher = Cipher.getInstance(ciphersuite);
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
			
			byte[] plainText = new byte[cipher.getOutputSize(ciphertext.length)];
			int ptLength = cipher.update(ciphertext, 0, ciphertext.length, plainText, 0);
			
			ptLength += cipher.doFinal(plainText, ptLength);
			System.out.println("Plaintext in HEX: " + Utils.toHex(plainText, ptLength) +
				" Size: " + ptLength);
			
			String msgoriginal = new String(plainText);
			
			System.out.println("----------------------------------------------");
			System.out.println("MSG Original Plaintext: " + msgoriginal);
			System.out.println("----------------------------------------------");
		}
	}
}

