package _1_2UsingECC;// Encryption using _1.2-UsingECC._1_2UsingECC.ECC
// Can use different EC Curves and parameterizations
// ... see specific documentation
// In general we are required to perform hybrid encryption using _1.2-UsingECC._1_2UsingECC.ECC.

// In the example we will use EC constructions form the BC provider
// ECIES in this exmaple works as a key-agreement followed by symmetric
// encryption. The idea is that we cannot directly encrypt nothingwith ECIES,
// which is the most common _1.2-UsingECC._1_2UsingECC.ECC method for encryption.
// In this case we couple the encryption w/ symmetric cipher.
// In fact, this is also best scheme for _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA encryption as well,
// in creating somesort of "dynamic encrypted envelopes", most of the time.

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

public class ECC {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {

        {
            // Input data to encrypt
            // (in this example given as the first argument)

            String input = "Hello world";
            byte[] inputBytes = input.getBytes();

            // or
            // byte[] input =
            //    new byte[] { (byte)0x12, (byte)0x34, (byte)0x56,
            //            (byte)0x78, (byte)0x78
            //                };

            // Try with this input data  w/ 17 bytes = 136 bits
            // What can you observe ?
            //  byte[] input =
            //  new byte[]
            //  {
            //  (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78,
            //  (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78,
            //  (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78,
            //  (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78
            //  ,0x11
            //  };

            // System.out.println("Input: " + new String(input));

            System.out.println("Input: " + input);

            //Cipher cipher=Cipher.getInstance("ECIES", "BC");
            Cipher cipher = Cipher.getInstance("ECIES", "BC");

            // Uhm .. what is the Cryotographic ECIES instance ? Interesting !

            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "BC");
            //KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");

            // What curves for _1.2-UsingECC._1_2UsingECC.ECC use ? Discussion ...
            // Have several ... ex., secp256k1 used currently in Bitcoin...
            // What are good (secure) or bad (not secure) Eliptic curves ?
            // Ongoing research: ex., https://safecurves.cr.yp.to

            // This curves for ex., are already supported in BC crypto providers
            //ECGenParameterSpec ecSpec= new ECGenParameterSpec("secp384r1");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");

            // Other curves available in other java-jce cryptoproviders

            //ECGenParameterSpec ecSpec= new ECGenParameterSpec("secp192r1");
            //ECGenParameterSpec ecSpec = new ECGenParameterSpec("sect571k1");
            //ECGenParameterSpec ecSpec= new ECGenParameterSpec("sect283k1");
            //ECGenParameterSpec ecSpec= new ECGenParameterSpec("secp256k1");
            //ECGenParameterSpec ecSpec= new ECGenParameterSpec("sect233k1");
            //ECGenParameterSpec ecSpec= new ECGenParameterSpec("sect409r1");

            // Other Curves in different cryptoproviders ...:
            // Curve25519, P384, Curve41417, Curve448-Goldilicks, M-511, P521

            kpg.initialize(ecSpec);

            // Generation of keypair for _1.2-UsingECC._1_2UsingECC.ECC (see from the theory)

            KeyPair ecKeyPair = kpg.generateKeyPair();
            System.out.println("Is it slow? No it is not ... ");

            // Encrypt (very similar as in _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA or ElGammal as you can see)

            cipher.init(Cipher.ENCRYPT_MODE, ecKeyPair.getPublic());
            byte[] cipherText = cipher.doFinal(inputBytes);
            System.out.println("Cipher: " + Utils3.toHex(cipherText));
            System.out.println("Len: " + cipherText.length + " Bytes");

            // Decrypt (very similar as in _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA or ElGammal as you can see)

            cipher.init(Cipher.DECRYPT_MODE, ecKeyPair.getPrivate());
            byte[] plaintext = cipher.doFinal(cipherText);

            System.out.println("plain : " + new String(plaintext));


        }
    }
}
