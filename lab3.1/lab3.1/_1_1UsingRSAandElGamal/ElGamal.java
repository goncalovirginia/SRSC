package _1_1UsingRSAandElGamal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.*;

/**
 * Basic _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA example.
 */
public class ElGamal {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(
            String[] args)
            throws Exception {
        // We will observe the use of _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA to encrypt/decrypt
        // data w/ possibe different sizes and limits of
        // these sizes depending on the key-sizes from keypair
        // generation. It is important to note that sizes of
        // objects are also relevant for security purposes
        // as well as the security relevante in using padding
        // parameterizations (using standard patterns)

        // input w/ 5 bytes = 40 bits
        byte[] input =
                //    new byte[] { (byte)0x12, (byte)0x34, (byte)0x56,
                //         (byte)0x78, (byte)0x78
                //         };

                new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x01
                };

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


        System.out.println("Input: " + Utils3.toHex(input));

        Cipher cipher = Cipher.getInstance("ELGamal/None/NoPadding", "BC");
        KeyPairGenerator g = KeyPairGenerator.getInstance("ElGamal", "BC");

        SecureRandom random = new SecureRandom();

        // As we can see, the use of the ElGammal algorithm is similar
        // to the use of _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA (in oter examples)

        // Keypair generation

        g.initialize(1024);

        KeyPair pair = g.generateKeyPair();
        Key pubKey = pair.getPublic();
        Key privKey = pair.getPrivate();

        // Encrypt

        cipher.init(Cipher.ENCRYPT_MODE, pubKey, random);
        byte[] cipherText = cipher.doFinal(input);
        System.out.println("cipher: " + Utils3.toHex(cipherText));

        // Decrypt

        cipher.init(Cipher.DECRYPT_MODE, privKey);
        byte[] plainText = cipher.doFinal(cipherText);
        System.out.println("plain : " + Utils3.toHex(plainText));
    }
}        
