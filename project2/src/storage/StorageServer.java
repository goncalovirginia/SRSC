package storage;

import ssl.AbstractSSLServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class StorageServer extends AbstractSSLServer {

	private final String filesRootPath;

	public StorageServer(ServerSocket serverSocket, String filesRootPath) throws IOException {
		super(serverSocket);
		this.filesRootPath = filesRootPath;
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("Usage: java StorageServer <properties file path>");
		}

		initProperties(args[0]);
		new StorageServer(initServerSocket(), properties.getProperty("filesRootPath")).run();
	}

	@Override
	protected void processConnection(Socket clientConnection) {
		sendFileBytes(clientConnection);

		try {
			clientConnection.close();
		} catch (IOException ignored) {
		}
	}

	private void sendFileBytes(Socket clientConnection) {
		try {
			DataOutputStream out = new DataOutputStream(clientConnection.getOutputStream());
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
				String filePath = getFilePath(in);
				byte[] fileBytes = getFileBytes(filePath);

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
				out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n");
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

	private byte[] getFileBytes(String filePath) throws IOException {
		System.out.println("Requested file: " + filePath);
		File f = new File(filesRootPath + File.separator + filePath);
		int length = (int) (f.length());
		if (length == 0) {
			throw new IOException("File length is zero: " + filePath);
		} else {
			FileInputStream fin = new FileInputStream(f);
			DataInputStream in = new DataInputStream(fin);

			byte[] bytecodes = new byte[length];
			in.readFully(bytecodes);
			return bytecodes;
		}
	}


}
