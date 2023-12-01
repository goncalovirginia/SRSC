package fileService;

import ssl.AbstractSSLServer;
import storage.StorageClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
			return;
		}

		new FileServer(args[0]).run();
	}

	@Override
	protected void processConnection(Socket clientConnection) {
		getFile(clientConnection);
	}

	private void getFile(Socket clientConnection) {
		try {
			DataOutputStream out = new DataOutputStream(clientConnection.getOutputStream());
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
				String storageHost = properties.getProperty("storageHost");
				int storagePort = Integer.parseInt(properties.getProperty("storagePort"));
				StorageClient storageClient = new StorageClient(storageHost, storagePort, properties);
				String filePath = getFilePath(in);
				System.out.println("Requested file: " + filePath);
				byte[] fileBytes = storageClient.getFile("/" + filePath);

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

	private String getFilePath(BufferedReader in) throws IOException {
		String line = in.readLine();
		String path = "";

		if (line.startsWith("GET /")) {
			line = line.substring(5, line.length() - 1).trim();
			int index = line.indexOf(' ');
			if (index != -1) {
				path = line.substring(0, index);
			}
		}

		// process the rest of header
		do {
			line = in.readLine();
		} while ((!line.isEmpty()) && (line.charAt(0) != '\r') && (line.charAt(0) != '\n'));

		if (path.isEmpty()) {
			throw new IOException("Malformed HTTP Header");
		}

		return path;
	}

}
