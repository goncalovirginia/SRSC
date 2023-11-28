package _2DH_Param_Generation;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;

public class DHParamGeneration {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(String[] argv) throws Exception {
		String algo = "DH"; //Change this to RSA, DSA ... Is similar

		KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(algo);

		// Uhm ...
		// See what happens if we use the Bouncy Castle Implementation
		//KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(algo, "BC");

		keyGenerator.initialize(4096);

		KeyPair kpair = keyGenerator.genKeyPair();
		PrivateKey priKey = kpair.getPrivate();
		PublicKey pubKey = kpair.getPublic();


		String frm = priKey.getFormat();
		System.out.println("Private key format: " + frm);

		frm = pubKey.getFormat();
		System.out.println("Public key format: " + frm);
		System.out.println("Public key alg.:" + pubKey.getAlgorithm());
		System.out.println("Diffie-Helman Public key parameters are:\n" + pubKey);
	}
}
