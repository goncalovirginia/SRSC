package encryptionDecryption.ex2.sendencrypt;
/**
 * Materiais/Labs para SRSC 20/21, Sem-1
 * hj
 **/

// SendEncrypt.java: cifrar e enviar uma mensagem usando um socket TCP

import encryptionDecryption.common.KeyRing;
import encryptionDecryption.common.Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


/**
 * Encrypt and send
 */
public class SendEncrypt {

    public static void main(String[] args) throws Exception {
        // Defaults para host e porto destino.
        // Se quizer passe em parametro a seguir

        if (args.length != 2) {
            System.out.println("Usar: SendEncrypt <hostname> <port>");
            System.exit(-1);
        }

        String desthost = args[0]; // Default;
        int destport = Integer.parseInt(args[1]); // Default;

        // Defaults para a Cifra... Se quizer passe em parametro a seguir
        String ciphersuite = "AES/CTR/NoPadding";

        byte[] ivBytes = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15
        };

        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        System.out.println("Host: " + desthost + " Port: " + destport);
        System.out.println("Ciphersuite a usar: " + ciphersuite);

        String plaintext = "INIT";
        SecretKey key = KeyRing.readSecretKey();

        for (; ; ) {
            plaintext = prompt("Mensagem Plaintext: ");
            if (plaintext.equals("tchau!")) break;
            byte[] ptextbytes = plaintext.getBytes();

            System.out.println("--------------------------------------------");
            System.out.println("Plaintext em HEX: "
                    + Utils.toHex(ptextbytes, ptextbytes.length));

            Cipher cipher = Cipher.getInstance(ciphersuite);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

            System.out.println("Mensagem cifrada a enviar (em HEX)...:");
            System.out.println(Utils.toHex(ciphertext, ciphertext.length) + " Size: " + ciphertext.length);
            System.out.println("----------------------------------------------");

            // Enviar cyphertext por um socket !
            Socket s = new Socket(desthost, destport);
            try {
                DataOutputStream os = new DataOutputStream(s.getOutputStream());
                os.writeInt(ciphertext.length);
                os.write(ciphertext);
                os.close();
            } finally {
                try {
                    s.close();
                } catch (Exception e) {

                    // ... Se quizer trate aqui a excepcap
                }
            }
        }
        System.exit(0);
    }


    /**
     * Mostra um prompt e captura a resposta numa String.
     */
    public static String prompt(String prompt) throws IOException {
        System.out.print(prompt);
        System.out.flush();
        BufferedReader input =
                new BufferedReader(new InputStreamReader(System.in));
        String response = input.readLine();
        System.out.println();
        return response;
    }
}

