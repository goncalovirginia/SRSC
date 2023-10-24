package MCHAT;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class SMP4PGMSPacket {

    private static final int PROTOCOL_VERSION = 2, NONCE_NUM_BYTES = 16;
    private static final Base64.Encoder base64Encoder = Base64.getEncoder();
    private static final Base64.Decoder base64Decoder = Base64.getDecoder();
    private static final Set<String> generatedNonces = new HashSet<>(), receivedUsernameNonces = new HashSet<>();
    private final StringBuilder packet;
    private final byte[] nonce;

    public SMP4PGMSPacket(byte[] data, int opCode) throws CryptoException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        packet = new StringBuilder();
        nonce = generateNonce(NONCE_NUM_BYTES);
        addHeader();
        addSignature(data, opCode);
        addPayload(data, opCode);
        addMacProof();
    }

    /**
     * Validates received SMP4PGMSPacket bytes and returns its decrypted payload data
     *
     * @param packetBytes Received SMP4PGMSPacket bytes
     * @return SMP4PGMSPacket payload data
     */
    public static byte[] receivePacket(byte[] packetBytes) throws SMP4PGMSPacketException, CryptoException, IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        String packetString = new String(packetBytes);
        String[] packetParts = packetString.split("\n");

        byte[] payload = base64Decoder.decode(packetParts[2]);
        byte[] payloadDecrypted = SymmetricCrypto.decrypt(payload);
        String[] payloadParts = new String(payloadDecrypted).split(";");
        String username = payloadParts[0];
        String opCode = payloadParts[1];
        byte[] nonce = base64Decoder.decode(payloadParts[2].getBytes());
        byte[] data = base64Decoder.decode(payloadParts[3].getBytes());

        validateHeader(packetParts[0], username);
        String headerUsernameOpcodeNonceData = packetParts[0] + username + opCode + new String(nonce) + new String(data);
        validateSignature(headerUsernameOpcodeNonceData, packetParts[1], username);
        validatePayload(nonce, data);
        validateMacProof(packetParts[3], packetString);

        return data;
    }
    
    private static void validateHeader(String header, String payloadUsername) throws SMP4PGMSPacketException, IOException, NoSuchAlgorithmException {
        String[] headerParts = header.split(";");
        int headerProtocolVersion = Integer.parseInt(headerParts[0]);
        long headerMagicNumber = Long.parseLong(headerParts[1]);
        String headerUsernameHashed = new String(base64Decoder.decode(headerParts[2].getBytes()));
        String payloadUsernameHashed = new String(Integrity.hash(payloadUsername.getBytes()));

        if (!SecurityConfig.validUsers.contains(payloadUsername)) {
            throw new SMP4PGMSPacketException("User " + payloadUsername + " not in verified users list.");
        }
        if (headerProtocolVersion != PROTOCOL_VERSION) {
            throw new SMP4PGMSPacketException("Header: Incorrect protocol version.");
        }
        if (headerMagicNumber != SecureMulticastChat.CHAT_MAGIC_NUMBER) {
            throw new SMP4PGMSPacketException("Header: Incorrect chat magic number.");
        }
        if (!headerUsernameHashed.equals(payloadUsernameHashed)) {
            throw new SMP4PGMSPacketException("Header: Hashed username does not match.");
        }
    }

    private static void validateSignature(String message, String signature, String username) throws SMP4PGMSPacketException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, InvalidKeyException {
        if (!Integrity.validateSignature(message.getBytes(), base64Decoder.decode(signature.getBytes()), username)) {
            throw new SMP4PGMSPacketException("Signature: Invalid signature.");
        }
    }

    private static void validatePayload(byte[] nonce, byte[] data) throws IOException, SMP4PGMSPacketException {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));
        dataInputStream.readLong();
        dataInputStream.readInt();
        String username = dataInputStream.readUTF();
        String nonceString = new String(nonce);
        String key = username + nonceString;

        if (receivedUsernameNonces.contains(key)) {
            throw new SMP4PGMSPacketException("Payload: Packet replaying detected.");
        }

        receivedUsernameNonces.add(key);
    }

    private static void validateMacProof(String macProof, String packetString) throws SMP4PGMSPacketException, NoSuchAlgorithmException, InvalidKeyException {
        String[] packetParts = packetString.split("\n");
        String headerSignaturePayload = packetParts[0] + "\n" + packetParts[1] + "\n" + packetParts[2] + "\n";
        String headerSignaturePayloadHMAC = new String(base64Encoder.encode(Integrity.hmac(headerSignaturePayload.getBytes())));

        if (!macProof.equals(headerSignaturePayloadHMAC)) {
            throw new SMP4PGMSPacketException("Mac Proof: Packet data has been tampered with.");
        }
    }

    private static byte[] generateNonce(int numBytes) {
        byte[] nonce128Bits = new byte[numBytes];
        String nonce128BitsString;

        do {
            new SecureRandom().nextBytes(nonce128Bits);
            nonce128BitsString = new String(nonce128Bits);
        } while (generatedNonces.contains(nonce128BitsString));

        generatedNonces.add(nonce128BitsString);
        return nonce128Bits;
    }

    public byte[] toByteArray() {
        return packet.toString().getBytes();
    }

    private void addHeader() throws NoSuchAlgorithmException {
        packet.append(PROTOCOL_VERSION);
        packet.append(";");
        packet.append(SecureMulticastChat.CHAT_MAGIC_NUMBER);
        packet.append(";");
        packet.append(new String(base64Encoder.encode(Integrity.hash(SecureMulticastChat.username.getBytes()))));
        packet.append("\n");
    }

    private void addPayload(byte[] data, int opCode) throws CryptoException {
        byte[] nonceBase64 = base64Encoder.encode(nonce);
        byte[] dataBase64 = base64Encoder.encode(data);

        String payload = SecureMulticastChat.username + ";" + opCode + ";" + new String(nonceBase64) + ";" + new String(dataBase64);
        byte[] encryptedPayload = SymmetricCrypto.encrypt(payload.getBytes());
        byte[] encryptedPayloadBase64 = base64Encoder.encode(encryptedPayload);

        packet.append(new String(encryptedPayloadBase64));
        packet.append("\n");
    }

    private void addSignature(byte[] data, int opCode) throws NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, InvalidKeyException {
        String headerUsernameOpcodeNonceData = packet.toString().split("\n")[0] + SecureMulticastChat.username + opCode + new String(nonce) + new String(data);
        byte[] signature = Integrity.sign(headerUsernameOpcodeNonceData.getBytes());

        packet.append(new String(base64Encoder.encode(signature)));
        packet.append("\n");
    }

    private void addMacProof() throws NoSuchAlgorithmException, InvalidKeyException {
        packet.append(new String(base64Encoder.encode(Integrity.hmac(packet.toString().getBytes()))));
        packet.append("\n");
    }

}
