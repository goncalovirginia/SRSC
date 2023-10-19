package _3OAEPPaddedRSA;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.*;

/**
 * _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA com uso de OAEP Padding + geracao de chave aleatoria
 * Notar que neste PAD e neste exemplo usa-se uma chave de no minimo 384 bits
 * Trata-se de um esquema de padding considerado dos mais seguros
 * OAEP - Optimal Asymmetric Encryption Padding
 * Esquema base:
 * M1=Mask( (H(P) || PS || 0x01 || M), S)
 * M2=Mask(S,M1)
 * Mp=0x00||M2||M1
 * Para Mask() ver MGF1
 */

public class OAEPPaddedRSAExample {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(
            String[] args)
            throws Exception {
        byte[] input = new byte[]{
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD,
                0x00, 0x00, (byte) 0xAB, (byte) 0xCD
        };


        System.out.println("Input size: " + input.length);

        Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
        SecureRandom random = Utils3.createFixedRandom();

        // gerar chaves
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");

        generator.initialize(2048, random);

        KeyPair pair = generator.generateKeyPair();
        Key pubKey = pair.getPublic();
        Key privKey = pair.getPrivate();


        System.out.println("input : " + Utils3.toHex(input));

        // Cifrar

        cipher.init(Cipher.ENCRYPT_MODE, pubKey, random);

        byte[] cipherText = cipher.doFinal(input);

        System.out.println("cipher: " + Utils3.toHex(cipherText));
        System.out.println("Length:" + cipherText.length);

        // Decifrar

        cipher.init(Cipher.DECRYPT_MODE, privKey);

        byte[] plainText = cipher.doFinal(cipherText);

        System.out.println("plain : " + Utils3.toHex(plainText));
        System.out.println("Length:" + plainText.length);
    }
}
