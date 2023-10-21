package MCHAT;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class SMP4PGMSPacket {
	
	private static final int PROTOCOL_VERSION = 1;
	
	private final StringBuilder packet;
	
	private static final Base64.Encoder base64Encoder = Base64.getEncoder();
	private static final Base64.Decoder base64Decoder = Base64.getDecoder();
	
	private static final Set<String> receivedNonces = new HashSet<>();
	
	public SMP4PGMSPacket(byte[] data) throws CryptoException, NoSuchAlgorithmException, InvalidKeyException {
		packet = new StringBuilder();
		addHeader();
		addPayload(data);
		addMacProof(data);
	}

	/**
	 * Validates received SMP4PGMSPacket bytes and returns its decrypted payload data
	 * @param packetBytes Received SMP4PGMSPacket bytes
	 * @return SMP4PGMSPacket payload data
	 */
	public static byte[] receivePacket(byte[] packetBytes) throws SMP4PGMSPacketException, CryptoException, IOException, NoSuchAlgorithmException, InvalidKeyException {
		String packetString = new String(packetBytes);
		String[] packetParts = packetString.split("\n");
		String payload = packetParts[1];
		String[] payloadParts = payload.split(";");
		byte[] encryptedNonce = base64Decoder.decode(payloadParts[0].getBytes());
		byte[] encryptedData = base64Decoder.decode(payloadParts[1].getBytes());
		byte[] nonce = SymmetricCrypto.decrypt(encryptedNonce);
		byte[] data = SymmetricCrypto.decrypt(encryptedData);

		validateHeader(packetParts[0], data);
		validatePayload(nonce, data);
		validateMacProof(packetParts[2], data);
		
		return data;
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
		packet.append(new String(base64Encoder.encode(Integrity.hmac(data))));
		packet.append("\n");
	}

	private static void validateHeader(String header, byte[] data) throws SMP4PGMSPacketException, IOException, NoSuchAlgorithmException {
		String[] headerParts = header.split(";");
		int headerProtocolVersion = Integer.parseInt(headerParts[0]);
		long headerMagicNumber = Long.parseLong(headerParts[1]);
		String headerHashedUsername = new String(base64Decoder.decode(headerParts[2].getBytes()));
		
		DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));
		dataInputStream.readLong();
		dataInputStream.readInt();
		String dataUsername = dataInputStream.readUTF();
		String dataHashedUsername = new String(Integrity.hash(dataUsername.getBytes()));
		
		if (headerProtocolVersion != PROTOCOL_VERSION) {
			throw new SMP4PGMSPacketException("Header: Incorrect protocol version.");
		}
		if (headerMagicNumber != SecureMulticastChat.CHAT_MAGIC_NUMBER) {
			throw new SMP4PGMSPacketException("Header: Incorrect chat magic number.");
		}
		if (!headerHashedUsername.equals(dataHashedUsername)) {
			throw new SMP4PGMSPacketException("Header: Hashed username does not match.");
		}
	}

	private static void validatePayload(byte[] nonce, byte[] data) throws IOException, SMP4PGMSPacketException {
		DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));
		dataInputStream.readLong();
		dataInputStream.readInt();
		String username = dataInputStream.readUTF();
		String nonceString = new String(nonce);
		String key = username + nonceString;
		
		if (receivedNonces.contains(key)) {
			throw new SMP4PGMSPacketException("Payload: Packet replaying detected.");
		}
		
		receivedNonces.add(key);
	}

	private static void validateMacProof(String macProof, byte[] data) throws SMP4PGMSPacketException, NoSuchAlgorithmException, InvalidKeyException {
		String dataHMAC = new String(base64Encoder.encode(Integrity.hmac(data)));
		
		if (!macProof.equals(dataHMAC)) {
			throw new SMP4PGMSPacketException("Mac Proof: Packet data has been tampered with.");
		}
	}

}
