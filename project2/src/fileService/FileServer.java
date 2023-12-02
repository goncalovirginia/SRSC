package fileService;

import ssl.AbstractSSLServer;
import storage.StorageClient;
import utils.HttpParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class FileServer extends AbstractSSLServer {

	private final String filesRootPath;
	private final StorageClient storageClient;
	//private final AuthClient authClient;
	//private final AccessControlClient accessControlClient;

	public FileServer(String propertiesFilePath) throws Exception {
		super(propertiesFilePath);
		filesRootPath = properties.getProperty("filesRootPath");
		/*
		String authHost = properties.getProperty("authHost");
		int authPort = Integer.parseInt(properties.getProperty("authPort"));
		authClient = new AuthClient(authHost, authPort, properties);

		String accessControlHost = properties.getProperty("accessControlHost");
		int accessControlPort = Integer.parseInt(properties.getProperty("accessControlPort"));
		accessControlClient = new AccessControlClient(accessControlHost, accessControlPort, properties);
		*/
		String storageHost = properties.getProperty("storageHost");
		int storagePort = Integer.parseInt(properties.getProperty("storagePort"));
		storageClient = new StorageClient(storageHost, storagePort, properties);
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: java FileServer <properties file path>");
			return;
		}

		new FileServer(args[0]).run();
	}

	@Override
	protected void processConnection(Socket clientConnection) throws IOException {
		DataOutputStream out = new DataOutputStream(clientConnection.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
		HttpParser httpParser = new HttpParser();
		httpParser.parseRequest(in);
		String[] requestLineParts = httpParser.getRequestLine().split(" ");

		switch (requestLineParts[0]) {
			case "POST" -> postFile(out, httpParser);
			case "GET" -> getFile(out, requestLineParts[1]);
		}
	}

	private void postFile(DataOutputStream out, HttpParser httpParser) {

	}

	private void getFile(DataOutputStream out, String filePath) {
		System.out.println("Requested file: " + filePath);
		try {
			try {
				byte[] fileBytes = storageClient.getFile(filePath);

				try {
					out.writeBytes("HTTP/1.0 200 OK\r\n");
					out.writeBytes("Content-Length: " + fileBytes.length + "\r\n");
					out.writeBytes("Content-Type: text/html\r\n\r\n");
					out.write(fileBytes);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				out.writeBytes("HTTP/1.0 500 " + e.getMessage() + "\r\n");
				out.writeBytes("Content-Type: text/html\r\n\r\n");
				out.flush();
			}
		} catch (IOException e) {
			System.out.println("error writing response: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
