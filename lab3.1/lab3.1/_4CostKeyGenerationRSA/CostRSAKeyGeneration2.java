import java.security.KeyPairGenerator;
import java.security.SecureRandom;

/**
 * _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA mas com geracao aleatoria de chaves
 */
public class CostRSAKeyGeneration2 {
    public static void main(
            String[] args)
            throws Exception {

        int size = Integer.parseInt(args[0]);
        SecureRandom random = new SecureRandom();

        // Criar par de chaves
        KeyPairGenerator generator = KeyPairGenerator.getInstance("_1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA", "BC");
        //KeyPairGenerator generator = KeyPairGenerator.getInstance("_1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA");

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


