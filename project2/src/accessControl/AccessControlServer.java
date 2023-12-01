package accessControl;

import auth.AuthServer;
import ssl.AbstractSSLServer;

import java.net.Socket;

public class AccessControlServer extends AbstractSSLServer {

	protected AccessControlServer(String propertiesFilePath) throws Exception {
		super(propertiesFilePath);
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: java AccessControlServer <properties file path>");
			return;
		}

		new AccessControlServer(args[0]).run();
	}

	@Override
	protected void processConnection(Socket clientConnection) {

	}
}
