package fileService;

import ssl.AbstractSSLClient;
import utils.HttpParser;

import java.io.*;
import java.util.Properties;

public class FileClient extends AbstractSSLClient {

	public FileClient(String targetHost, int targetPort, Properties properties) throws Exception {
		super(targetHost, targetPort, properties);
	}

	public boolean login(String username, String password) {
		return true;
	}

	public byte[] getFile(String username, String filePath) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.println("GET /" + username + "/" + filePath + " HTTP/1.0");
		out.println();
		out.flush();

		if (out.checkError()) {
			System.out.println("SSLSocketClient: java.io.PrintWriter error");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		HttpParser httpParser = new HttpParser();
		httpParser.parseRequest(in);

		if (!httpParser.getRequestLine().endsWith("OK")) {
			return new byte[]{};
		}

		return httpParser.getBody().getBytes();
	}



}
