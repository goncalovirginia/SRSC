package storage;

import ssl.AbstractSSLServer;
import utils.HttpParser;

import java.io.*;
import java.net.Socket;

public class StorageServer extends AbstractSSLServer {

	private final String filesRootPath;

	public StorageServer(String propertiesFilePath) throws Exception {
		super(propertiesFilePath);
		filesRootPath = properties.getProperty("filesRootPath");
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: java StorageServer <properties file path>");
			return;
		}

		new StorageServer(args[0]).run();
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

	private byte[] getFileBytes(String filePath) throws IOException {
		File f = new File(filesRootPath + filePath);
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
