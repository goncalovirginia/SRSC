package _4Wrapping_RSA_Keys;

import javax.crypto.Cipher;
import java.security.*;

/**
 * Exemplo para proteger uma chave _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA (privada) wrapped com uma chave AES.
 * A ideia sera usar a chave AES como wrapping key da chave _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA (chave wrapped)
 */
public class AESWrapRSAExample {
    public static void main(
            String[] args)
            throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        SecureRandom random = new SecureRandom();

        KeyPairGenerator fact = KeyPairGenerator.getInstance("_1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA", "BC");
        fact.initialize(512, new SecureRandom());
        // fact.initialize(2048, new SecureRandom());

        KeyPair keyPair = fact.generateKeyPair();
        Key wrapKey = Utils3.createKeyForAES(256, random);


        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println();
        System.out.println("A chave privada (key to be wrapped) :\n" + Utils3.toHex(privateKey.getEncoded()));
        System.out.println();
        System.out.println("A chave secreta (wrapping key):\n" + Utils3.toHex(wrapKey.getEncoded()));

        // wraping da chave _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA
        cipher.init(Cipher.WRAP_MODE, wrapKey);

        byte[] wrappedKey = cipher.wrap(keyPair.getPrivate());

        System.out.println();
        System.out.println("A chave privada protegida (wrapped key):\n" + Utils3.toHex(wrappedKey));


        System.out.println();
        System.out.println("Vamos recuperar a chave privada (dada a wrapped key)");

        // unwraping da chave _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA
        cipher.init(Cipher.UNWRAP_MODE, wrapKey);

        Key key = cipher.unwrap(wrappedKey, "_1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA", Cipher.PRIVATE_KEY);

        if (keyPair.getPrivate().equals(key)) {
            System.out.println("\nOk, Chave privada recuperada com sucesso.\n");
        } else {
            System.out.println("\n ... Erro na recuperacao da chave.\n");
        }
    }
}
