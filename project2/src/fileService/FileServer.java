package fileService;

import ssl.AbstractSSLServer;

import java.io.IOException;
import java.net.Socket;

public class FileServer extends AbstractSSLServer {

	private final String filesRootPath;

	public FileServer(String propertiesFilePath) throws Exception {
		super(propertiesFilePath);
		filesRootPath = properties.getProperty("filesRootPath");
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: java FileServer <properties file path>");
		}

		new FileServer(args[0]).run();
	}

	@Override
	protected void processConnection(Socket clientConnection) {
		//TODO

		try {
			clientConnection.close();
		} catch (IOException ignored) {
		}
	}

}
