package MCHAT;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Integrity {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] hash(String algorithm, byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(algorithm).digest(data);
    }

    public static byte[] hash(byte[] data) throws NoSuchAlgorithmException {
        return hash(SecurityConfig.HASH_ALGORITHM, data);
    }

    public static byte[] hmac(String algorithm, byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hMac = Mac.getInstance(algorithm);
        Key hMacKey = new SecretKeySpec(key, algorithm);
        hMac.init(hMacKey);
        return hMac.doFinal(data);
    }

    public static byte[] hmac(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        return hmac(SecurityConfig.MAC_ALGORITHM, SecurityConfig.MAC_KEY, data);
    }

    public static byte[] signature(byte[] data) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(SecurityConfig.ELLIPTIC_CURVE_ALGORITHM);
        kpg.initialize(ecSpec, new SecureRandom());
        KeyPair keyPair = kpg.generateKeyPair();

        Signature signature = Signature.getInstance(SecurityConfig.SIGNATURE_ALGORITHM);
        signature.initSign(keyPair.getPrivate(), new SecureRandom());
        signature.update(data);

        return signature.sign();
    }

}
