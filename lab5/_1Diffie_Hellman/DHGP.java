package _1Diffie_Hellman;//
// SRSC 21-22 Labs
//

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.spec.DHParameterSpec;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.Security;

/**
 * Weight for the generation of DH initial Parameters
 * Remember from the theory:
 * Computation of the primitive root G for a Prime Number P
 */
public class DHGP {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(String[] args) throws Exception {
		AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH", "BC");
		// paramGen.init(1024);
		paramGen.init(256);
		AlgorithmParameters params = paramGen.generateParameters();

		DHParameterSpec dhSpec = params.getParameterSpec(DHParameterSpec.class);

		System.out.println("Done ! \nG = " + dhSpec.getG() + "\nP = " + dhSpec.getP());
	}

}

