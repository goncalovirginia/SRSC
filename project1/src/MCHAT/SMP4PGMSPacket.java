package MCHAT;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class SMP4PGMSPacket {
	
	private static final int PROTOCOL_VERSION = 1;
	
	private StringBuilder packet;
	
	public SMP4PGMSPacket(byte[] data) throws CryptoException {
		packet = new StringBuilder();
		addControlHeader();
		addPayload(data);
		addMacProof();
	}
	
	public boolean validatePacket() {
		return true;
	}
	
	public byte[] toBytes() {
		return packet.toString().getBytes();
	}
	
	public byte[] getPayloadData() {
		return null
	}
	
	private void addControlHeader() {
		packet.append(PROTOCOL_VERSION);
		packet.append(";");
		packet.append(SecureMulticastChat.CHAT_MAGIC_NUMBER);
		packet.append(";");
		packet.append(SecureMulticastChat.username); //TODO Hash the username
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
	
	private void addMacProof() {
	
	}
	
	public static void main(String[] args) {
		byte[] random128bit = new byte[16];
		new SecureRandom().nextBytes(random128bit);
		
		System.out.println(Arrays.toString(random128bit));
		String base64 = new String(Base64.getEncoder().encode(random128bit));
		System.out.println(base64);
		System.out.println(Arrays.toString(Base64.getDecoder().decode(base64)));
	}

}
