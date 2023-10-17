import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

/**
 * _1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA mas com geracao aleatoria de chaves
 */
public class CostRSAKeyGeneration {
    public static void main(
            String[] args)
            throws Exception {

        SecureRandom random = new SecureRandom();

        // Criar par de chaves
        KeyPairGenerator generator = KeyPairGenerator.getInstance("_1.1-UsingRSAandElGamal._1_1UsingRSAandElGamal.RSA", "BC");
        generator.initialize(2048, random);
        // generator.initialize(4096, random);
        KeyPair pair = generator.generateKeyPair();
    }
}


