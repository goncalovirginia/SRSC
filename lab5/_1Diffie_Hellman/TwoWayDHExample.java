package _1Diffie_Hellman;//
// SRSC 21-22 Labs
//

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Security;

/**
 * Two party key agreement using Diffie-Hellman
 * Here we use pre-computed values for the primitive root G and
 * Prime Number P, that will be used for the dynamic key-agreement
 * shown by the example
 */
public class TwoWayDHExample {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	// Parametro para o gerador do Grupo de Cobertura de P
	private static final BigInteger g512 = new BigInteger(
			"153d5d6172adb43045b68ae8e1de1070b6137005686d29d3d73a7"
					+ "749199681ee5b212c9b96bfdcfa5b20cd5e3fd2044895d609cf9b"
					+ "410b7a0f12ca1cb9a428cc", 16);
	// Um grande numero primo P
	private static final BigInteger p512 = new BigInteger(
			"9494fec095f3b85ee286542b3836fc81a5dd0a0349b4c239dd387"
					+ "44d488cf8e31db8bcb7d33b41abb9e5a33cca9144b1cef332c94b"
					+ "f0573bf047a3aca98cdf3b", 16);

	public static void main(String[] args) throws Exception {
		DHParameterSpec dhParams = new DHParameterSpec(p512, g512);

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");
		// Parecido com a forma de utilizacao de cripto assimetrica mas
		// com o parametro DH passado em vez de RSA

		//        keyGen.initialize(dhParams, UtilsDH.createFixedRandom());
		keyGen.initialize(dhParams);
		// take care about this. In this acse is just an example
		// Discussion about the consequences...

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