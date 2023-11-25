//
// SRSC 21-22 Labs
//

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Genereration of Diffie-Hellman Keypair (Java)
 * Provate DH number is represented in a standard format: PKCS#8
 * (internally represented in Base64)
 * Public DH nyumber is represented in standard format X509
 * as exportable format to be used as needed
 */
public class DH {

	public static void main(
			String[] args)
			throws Exception {

		//  Base64.Encoder enc=Base64.getEncoder();
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");
		//KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
		keyGen.initialize(1024);

		KeyPair aPair = keyGen.generateKeyPair();
		PrivateKey aPrivKey = aPair.getPrivate();
		PublicKey aPubKey = aPair.getPublic();
		String format = aPrivKey.getFormat();

		System.out.println("\nPrivate Key Format: " + format);

		format = aPubKey.getFormat();
		System.out.println("\nPublic Key Format: " + format);
		System.out.println("\nPublic Key : " + aPubKey);

	}

}

