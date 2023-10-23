package MCHAT;

import _2ECCDSA_Signatures.Utils3;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.HexFormat;

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

    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(SecurityConfig.ELLIPTIC_CURVE_ALGORITHM);
        kpg.initialize(ecSpec, new SecureRandom());
        return kpg.generateKeyPair();
    }

    public static byte[] sign(byte[] data) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
        KeyPair keyPair = generateKeyPair();
        Signature signature = Signature.getInstance(SecurityConfig.SIGNATURE_ALGORITHM);
        signature.initSign(keyPair.getPrivate(), new SecureRandom());
        signature.update(data);
        return signature.sign();
    }
    
    public static boolean validateSignature(byte[] message, byte[] signatureBytes, String username) throws NoSuchAlgorithmException, SignatureException {
        String publicKey = SecurityConfig.publicKeys.get(username)[2];
        Signature signature = Signature.getInstance(SecurityConfig.SIGNATURE_ALGORITHM);
        signature.initVerify();
        signature.update(message);
        
        return signature.verify(signatureBytes);
    }

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        KeyPair keyPair = generateKeyPair();
        System.out.println(keyPair.getPrivate().getFormat());
        System.out.println(HexFormat.of().formatHex(keyPair.getPrivate().getEncoded()));
        System.out.println(keyPair.getPublic().getFormat());
        System.out.println(HexFormat.of().formatHex(keyPair.getPublic().getEncoded()));
    }

}
