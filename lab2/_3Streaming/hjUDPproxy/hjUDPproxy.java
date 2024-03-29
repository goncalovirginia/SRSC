package _3Streaming.hjUDPproxy;

/* hjUDPproxy, 20/Mar/18
 *
 * This is a very simple (transparent) UDP proxy
 * The proxy can listening on a remote source (server) UDP sender
 * and transparently forward received datagram packets in the
 * delivering endpoint
 *
 * Possible Remote listening endpoints:
 *    Unicast IP address and port: configurable in the file config.properties
 *    Multicast IP address and port: configurable in the code
 *
 * Possible local listening endpoints:
 *    Unicast IP address and port
 *    Multicast IP address and port
 *       Both configurable in the file config.properties
 */

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

class hjUDPproxy {
	public static void main(String[] args) throws Exception {
		InputStream inputStream = new FileInputStream("lab2/_3Streaming/hjUDPproxy/config.properties");
		Properties properties = new Properties();
		properties.load(inputStream);
		String remote = properties.getProperty("remote");
		String destinations = properties.getProperty("localdelivery");

		SocketAddress inSocketAddress = parseSocketAddress(remote);
		Set<SocketAddress> outSocketAddressSet = Arrays.stream(destinations.split(",")).map(s -> parseSocketAddress(s)).collect(Collectors.toSet());

		DatagramSocket inSocket = new DatagramSocket(inSocketAddress);
		DatagramSocket outSocket = new DatagramSocket();
		byte[] buffer = new byte[4 * 1024];

		while (true) {
			DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
			inSocket.receive(inPacket);  // if remote is unicast

			System.out.print("*");
			for (SocketAddress outSocketAddress : outSocketAddressSet) {
				outSocket.send(new DatagramPacket(buffer, inPacket.getLength(), outSocketAddress));
			}
		}
	}

	private static InetSocketAddress parseSocketAddress(String socketAddress) {
		String[] split = socketAddress.split(":");
		String host = split[0];
		int port = Integer.parseInt(split[1]);
		return new InetSocketAddress(host, port);
	}
}
