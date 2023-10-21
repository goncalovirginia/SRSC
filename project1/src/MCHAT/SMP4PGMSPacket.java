package MCHAT;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class SMP4PGMSPacket {
	
	private static final int PROTOCOL_VERSION = 1;
	
	private final StringBuilder packet;
	
	private static final Base64.Encoder base64Encoder = Base64.getEncoder();
	private static final Base64.Decoder base64Decoder = Base64.getDecoder();
	
	public SMP4PGMSPacket(byte[] data) throws CryptoException, NoSuchAlgorithmException, InvalidKeyException {
		packet = new StringBuilder();
		addControlHeader();
		addPayload(data);
		addMacProof(data);
	}

	/**
	 * Validates received SMP4PGMSPacket bytes and returns its decrypted payload data
	 * @param packetBytes Received SMP4PGMSPacket bytes
	 * @return SMP4PGMSPacket payload data
	 */
	public static byte[] receivePacket(byte[] packetBytes) throws SMP4PGMSPacketException, CryptoException {
		String packetString = new String(packetBytes);
		String[] packetParts = packetString.split("\n");

		if (!(validateHeader(packetParts[0]) && validatePayload(packetParts[1]) && validateMacProof(packetParts[2]))) {
			throw new SMP4PGMSPacketException("Invalid packet");
		}
		
		String payload = packetParts[1];
		String[] payloadParts = payload.split(";");
		byte[] encryptedNonce = base64Decoder.decode(payloadParts[0].getBytes());
		byte[] encryptedData = base64Decoder.decode(payloadParts[1].getBytes());
		byte[] nonce = SymmetricCrypto.decrypt(encryptedNonce);
		byte[] data = SymmetricCrypto.decrypt(encryptedData);
		
		return data;
	}
	
	public byte[] toByteArray() {
		return packet.toString().getBytes();
	}
	
	private void addControlHeader() throws NoSuchAlgorithmException {
		packet.append(PROTOCOL_VERSION);
		packet.append(";");
		packet.append(SecureMulticastChat.CHAT_MAGIC_NUMBER);
		packet.append(";");
		packet.append(new String(Integrity.hash(SecureMulticastChat.username.getBytes())));
		packet.append("\n");
	}
	
	private void addPayload(byte[] data) throws CryptoException {
		byte[] random128Bits = new byte[16];
		new SecureRandom().nextBytes(random128Bits);
		byte[] random128BitsEncrypted = SymmetricCrypto.encrypt(random128Bits);
		byte[] random128BitsEncryptedBase64 = base64Encoder.encode(random128BitsEncrypted);
		
		byte[] dataEncrypted = SymmetricCrypto.encrypt(data);
		byte[] dataEncryptedBase64 = base64Encoder.encode(dataEncrypted);
		
		String payload = new String(random128BitsEncryptedBase64) + ";" + new String(dataEncryptedBase64);
		
		packet.append(payload);
		packet.append("\n");
	}
	
	private void addMacProof(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
		packet.append(new String(Base64.getEncoder().encode(Integrity.hmac(data))));
		packet.append("\n");
	}

	private static boolean validateHeader(String header) {
		String[] headerParts = header.split(";");
		int headerProtocolVersion = Integer.parseInt(headerParts[0]);
		long headerMagicNumber = Long.parseLong(headerParts[1]);
		
		return headerProtocolVersion == PROTOCOL_VERSION && headerMagicNumber == SecureMulticastChat.CHAT_MAGIC_NUMBER;
	}

	private static boolean validatePayload(String payload) {
		return true;
	}

	private static boolean validateMacProof(String macProof) {
		return true;
	}

}
