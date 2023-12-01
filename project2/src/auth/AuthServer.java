package auth;

import ssl.AbstractSSLServer;
import storage.StorageServer;

import java.net.Socket;

public class AuthServer extends AbstractSSLServer {

	public AuthServer(String propertiesFilePath) throws Exception {
		super(propertiesFilePath);
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: java AuthServer <properties file path>");
			return;
		}

		new AuthServer(args[0]).run();
	}

	@Override
	protected void processConnection(Socket clientConnection) {

	}
}
