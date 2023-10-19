package _4CostKeyGenerationRSA;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

/**
 * _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA mas com geracao aleatoria de chaves
 */
public class CostRSAKeyGeneration2 {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(
            String[] args)
            throws Exception {

        int size = 256;
        SecureRandom random = new SecureRandom();

        // Criar par de chaves
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        //KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        long totalMillis = 0L;
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            generator.initialize(size, random);
            generator.generateKeyPair();
            long end = System.currentTimeMillis();
            long cost = end - start;
            System.out.println("Observed times: " + cost + " ms");
        }
    }
}


