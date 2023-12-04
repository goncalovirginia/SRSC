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
	private final String storageHost, authHost, accessControlHost;
	private final int storagePort, authPort, accessControlPort;

	public FileServer(String propertiesFilePath) throws Exception {
		super(propertiesFilePath);

		filesRootPath = properties.getProperty("filesRootPath");

		authHost = properties.getProperty("authHost");
		authPort = Integer.parseInt(properties.getProperty("authPort"));
		accessControlHost = properties.getProperty("accessControlHost");
		accessControlPort = Integer.parseInt(properties.getProperty("accessControlPort"));
		storageHost = properties.getProperty("storageHost");
		storagePort = Integer.parseInt(properties.getProperty("storagePort"));
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
		BufferedReader in = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
		DataOutputStream out = new DataOutputStream(clientConnection.getOutputStream());
		HttpParser httpParser = new HttpParser();
		httpParser.parse(in);
		String[] requestLineParts = httpParser.getRequestLine().split(" ");

		switch (requestLineParts[0]) {
			case "POST" -> postFile(out, httpParser);
			case "GET" -> getFile(out, requestLineParts[1]);
		}
	}

	private void postFile(DataOutputStream out, HttpParser httpParser) {
		System.out.println("postFile: " + httpParser.getRequestLine().split(" ")[1]);
		try {
			try {
				StorageClient storageClient = new StorageClient(storageHost, storagePort, properties);
				String requestResult = storageClient.postFile(httpParser);
				try {
					out.writeBytes(requestResult + "\r\n\r\n");
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				out.writeBytes("HTTP/1.0 500 " + e.getMessage() + "\r\n\r\n");
				out.flush();
			}
		} catch (IOException e) {
			System.out.println("error writing response: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void getFile(DataOutputStream out, String filePath) {
		System.out.println("getFile: " + filePath);
		try {
			try {
				StorageClient storageClient = new StorageClient(storageHost, storagePort, properties);
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
