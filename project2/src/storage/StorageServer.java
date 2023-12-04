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
				saveFileToDisk(httpParser);
				try {
					out.writeBytes("HTTP/1.0 204 OK\r\n\r\n");
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n\r\n");
				out.flush();
			}
		} catch (IOException e) {
			System.out.println("error writing response: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void saveFileToDisk(HttpParser httpParser) throws IOException {
		String filePath = httpParser.getRequestLine().split(" ")[1];
		byte[] data = httpParser.getBody().getBytes();

		OutputStream out = new FileOutputStream(filesRootPath + filePath);
		out.write(data);
		out.close();
	}

	private void getFile(DataOutputStream out, String filePath) {
		System.out.println("getFile: " + filePath);
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
				out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n\r\n");
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
		}

		DataInputStream in = new DataInputStream(new FileInputStream(f));
		byte[] fileBytes = new byte[length];
		in.readFully(fileBytes);
		return fileBytes;
	}


}
