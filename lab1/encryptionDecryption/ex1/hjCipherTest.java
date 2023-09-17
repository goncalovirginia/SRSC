/**
 * Material/Labs SRSC 21/22, Sem-1
 * Henrique Domingos
 **/

import common.Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.util.Base64;

// Very simple (initial) code to check encryption/decryption with
// Symmetric Cryptographic Algorithms in Java.
// Support from JCA/JCE
// (Java Cryptography Architecture / JCE Java Cryptographic Extensions).
// It is just to test ...
// In next weeks we will learn many details on the correct use (practical)
// of Crypto in Java using the JCE

public class hjCipherTest {

    public static void main(String[] args) {
        try {
            if (args.length != 3) {
                System.out.println("Usar: hjCipherTest data alg cipherconf");
                System.out.println("  ex: hjCipherTest topsecretdata AES AES/ECB/PKCS5Padding");
                System.out.println("  ex: hjCipherTest topsecretdata AES DESede/CBC/PKCS5Padding");
                System.out.println("  ex: hjCipherTest topsecretdata AES AES/CBC/PKCS5Padding");
                System.out.println("  ex: hjCipherTest topsecretdata AES AES/CTR/NoPadding");
                System.out.println("  ex: hjCipherTest topsecretdata BLOWFISH BLOWFISH/CFB/NoPadding");
                System.out.println("  ex: hjCipherTest topsecretdata AES AES/OFB/PKCS5Padding");
                System.exit(-1);
            }

            // Important: different symmetric algorithms use different
            // sizes for base-operated blocks and also different keysizes

            // Can also note that in general you need to operate the
            // symmetric encryption using a standardized padding method
            // In above examples ths is the role pk PKCS#5 for example.
            // PKCS#5 in a standardized padding.


            // Initialization Vector (IV)
            // In some situations (certain Encryption Modes of
            // Operation, we need IVs as initial parameters for
            // Symmetric encryption/decryption
            // Important: must use IV size depending on the block size
            // for the used algorithm
            // Ex for Blowfish or DESede for ex., processing blocks are 64 bits
            // so the required iv must be 64 bits
            // Ex for AES, RC6 for ex., processing blocks are 128 bits
            // so the required iv must be 128 bits

            byte[] iv = {0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
            //byte[] iv = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};

            IvParameterSpec dps = new IvParameterSpec(iv);
            // Comment last line for modes that don't operate with IVs

            // We need a key
            // We will have different ways ...
            // We can use a key that we already have ... ex, stored in a
            // a file (as our keyring) or in a keystore (standard java file repository)

            // We can also to generate the key to use ...
            // Or we can obtain it from a "trusted and secure"
            // key distribution service /or protocol)
            // Here we will generate the key (on-the-fly)

            KeyGenerator kg = KeyGenerator.getInstance(args[1]);

            // Initialize the crypto suite parameterization
            Cipher c = Cipher.getInstance(args[2]);

            Key key = kg.generateKey();

            // If you want to see the generated Key bytes and size ...
            byte[] keyBytes = key.getEncoded();

            byte[] keyBytesBase64 = Base64.getEncoder().encode(keyBytes);

            System.out.println();
            System.out.println("Key in Base64:\n" + new String(keyBytesBase64));
            System.out.println("Key Size in Bytes:\n" + keyBytes.length);

            System.out.println("Encrypt...:");
            c.init(Cipher.ENCRYPT_MODE, key, dps);

            byte[] plaintext = args[0].getBytes(); // input plaintext
            byte[] ciphertext = c.doFinal(plaintext);  // out ciphertext

            byte[] encryptedBase64 = Base64.getEncoder().encode(ciphertext);

            System.out.println("Ciphertext in Base64:\n" + new String(encryptedBase64));
            System.out.println("Ciphertext in Hexadecimal:\n" + Utils.toHex(ciphertext));

            System.out.println("Decrypt....: ");
            c.init(Cipher.DECRYPT_MODE, key, dps);

            byte[] output = c.doFinal(ciphertext);
            System.out.println("Initial input plaintext: " + new String(output));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
