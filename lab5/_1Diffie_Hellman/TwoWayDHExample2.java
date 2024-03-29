package _1Diffie_Hellman;//
// SRSC 21-22 Labs
//


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyAgreement;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Security;

/**
 * Two party key agreement using Diffie-Hellman
 */
public class TwoWayDHExample2 {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(String[] args) throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");
		//KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
		keyGen.initialize(512);

		// set up
		// Simulation in A side
		KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
		KeyPair aPair = keyGen.generateKeyPair();

		aKeyAgree.init(aPair.getPrivate());

		// Simulation in B side
		KeyAgreement bKeyAgree = KeyAgreement.getInstance("DH", "BC");
		KeyPair bPair = keyGen.generateKeyPair();

		bKeyAgree.init(bPair.getPrivate());

		// two party agreement
		// B receives the A public value and A receives the B Public value

		// Then A generates
		aKeyAgree.doPhase(bPair.getPublic(), true);

		// B generates
		bKeyAgree.doPhase(aPair.getPublic(), true);

		//      generate the key bytes
		MessageDigest hash = MessageDigest.getInstance("SHA1", "BC");

		// Then A generates the final agreement key
		byte[] aShared = hash.digest(aKeyAgree.generateSecret());

		// Then B generates the final agreement key
		byte[] bShared = hash.digest(bKeyAgree.generateSecret());

		// ... and as you can see, the final key is the same :-)
		System.out.println("A: " + UtilsDH.toHex(aShared)); // printed by A
		System.out.println("B: " + UtilsDH.toHex(bShared)); // printed by B
	}
}
