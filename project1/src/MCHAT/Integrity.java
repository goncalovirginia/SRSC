package MCHAT;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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

    public static byte[] sign(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        PrivateKey privateKey = getPrivateKey();
        Signature signature = Signature.getInstance(SecurityConfig.SIGNATURE_ALGORITHM);
        signature.initSign(privateKey, new SecureRandom());
        signature.update(data);
        return signature.sign();
    }

    public static boolean validateSignature(byte[] message, byte[] signatureBytes, String username) throws NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, InvalidKeyException {
        PublicKey publicKey = getPublicKey(username);
        Signature signature = Signature.getInstance(SecurityConfig.SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
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

    private static PublicKey getPublicKey(String username) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String publicKeyHex = SecurityConfig.publicKeys.get(username)[1];
        byte[] byteKey = HexFormat.of().parseHex(publicKeyHex);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(byteKey);
        KeyFactory kf = KeyFactory.getInstance("ECDSA");
        return kf.generatePublic(x509EncodedKeySpec);
    }

    private static PrivateKey getPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String privateKeyHex = SecurityConfig.PRIVATE_KEY;
        byte[] byteKey = HexFormat.of().parseHex(privateKeyHex);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(byteKey);
        KeyFactory kf = KeyFactory.getInstance("ECDSA");
        return kf.generatePrivate(pkcs8EncodedKeySpec);
    }

}
