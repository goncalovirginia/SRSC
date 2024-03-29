package _2ECCDSA_Signatures;
// Seguranca de Sistemas e Redes de Computadores
// 20017/2018, hj@fct.unl.pt

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

/**
 * Geracao e verificacao de assinaturas digitais
 * com _1.2-UsingECC._1_2UsingECC.ECC DSA
 */
public class ECCDSA {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(
			String[] args)
			throws Exception {

		byte[] message = "important msg to sign with ECC/DSA".getBytes();

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA", "BC");

		// What curves for _1.2-UsingECC._1_2UsingECC.ECC use ? Discussion ...
		// Have several ... ex., secp256k1 used currently in Bitcoin...                // What are good (secure) or bad (not secure) Eliptic curves ?                 // Ongoing research: ex., https://safecurves.cr.yp.to

		// ECGenParameterSpec ecSpec= new ECGenParameterSpec("secp384r1");
		// ECGenParameterSpec ecSpec= new ECGenParameterSpec("secp256r1");
		// ECGenParameterSpec ecSpec= new ECGenParameterSpec("secp192r1");
		// ECGenParameterSpec ecSpec= new ECGenParameterSpec("sect571k1");
		// ECGenParameterSpec ecSpec= new ECGenParameterSpec("sect283k1");

		//ECGenParameterSpec ecSpec= new ECGenParameterSpec("secp256k1");
		// ECGenParameterSpec ecSpec= new ECGenParameterSpec("sect233k1");
		// ECGenParameterSpec ecSpec= new ECGenParameterSpec("sect409r1");
		ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
		// ECGenParameterSpec ecSpec= new ECGenParameterSpec("sect409r1");

		// Other Curves n different cryptoproviders ...:
		// Curve25519, P384, Curve41417, Curve448-Goldilicks, M-511, P521

		kpg.initialize(ecSpec, new SecureRandom()); // why ?

		// Gerar par de chaves _1.2-UsingECC._1_2UsingECC.ECC na curva parameterizada
		KeyPair keyPair = kpg.generateKeyPair();

		// Ok temos tudo para usar nas assinaturas ...

		//Signature signature = Signature.getInstance("SHA512withECDSA");
		Signature signature = Signature.getInstance("SHA224/ECDSA", "BC");
		signature.initSign(keyPair.getPrivate(), new SecureRandom());
		signature.update(message);

		byte[] sigBytes = signature.sign();
		System.out.println("ECDSA signature bytes: " + Utils3.toHex(sigBytes));
		System.out.println("Size of Signature    : " + sigBytes.length);

		// Verificar - neste caso estamos a obter a chave publica do par mas
		// em geral usamos a chave publica que previamente conhecemos de
		// quem assinou.
		//
		signature.initVerify(keyPair.getPublic());
		signature.update(message);

		if (signature.verify(sigBytes)) {
			System.out.println("Assinatura ECDSA validada - reconhecida");
		} else {
			System.out.println("Assinatura nao reconhecida");
		}
	}
}
