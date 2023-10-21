package MCHAT;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class SMP4PGMSPacket {
	
	private static final int PROTOCOL_VERSION = 1;
	
	private final StringBuilder packet;
	
	public SMP4PGMSPacket(byte[] data) throws CryptoException, NoSuchAlgorithmException, InvalidKeyException {
		packet = new StringBuilder();
		addControlHeader();
		addPayload(data);
		addMacProof(data);
	}

	/**
	 * Validates received SMP4PGMSPacket bytes and returns its payload data
	 * @param packetBytes Received SMP4PGMSPacket bytes
	 * @return SMP4PGMSPacket payload data
	 */
	public static byte[] receivePacket(byte[] packetBytes) throws SMP4PGMSPacketException {
		String packetString = new String(packetBytes);
		String[] packetParts = packetString.split("\n");

		if (!(validateHeader(packetParts[0]) && validatePayload(packetParts[1]) && validateMacProof(packetParts[2]))) {
			throw new SMP4PGMSPacketException("Invalid packet");
		}

		System.out.println(packetString);
		return packetParts[1].getBytes();
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
		String random128bitsBase64 = new String(Base64.getEncoder().encode(random128Bits));
		
		String payload = random128bitsBase64 + ";" + new String(data);
		String encryptedPayload = new String(SymmetricCrypto.encrypt(payload.getBytes()));
		
		packet.append(encryptedPayload);
		packet.append("\n");
	}
	
	private void addMacProof(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
		packet.append(new String(Base64.getEncoder().encode(Integrity.hmac(data))));
	}
	
	public static void main(String[] args) {
		byte[] random128bit = new byte[16];
		new SecureRandom().nextBytes(random128bit);

		System.out.println(Arrays.toString(random128bit));
		System.out.println(new String(new String(random128bit).getBytes()));

		String base64 = new String(Base64.getEncoder().encode(random128bit));
		System.out.println(base64);
		System.out.println(Arrays.toString(Base64.getDecoder().decode(base64)));
	}

	private static boolean validateHeader(String header) {
		String[] headerParts = header.split(";");
		int headerProtocolVersion = Integer.parseInt(headerParts[0]);
		long headerMagicNumber = Long.parseLong(headerParts[1]);
		String headerUsernameHash = headerParts[2];



		return true;
	}

	private static boolean validatePayload(String payload) {
		return true;
	}

	private static boolean validateMacProof(String macProof) {
		return true;
	}

}
