package storage;

import ssl.AbstractSSLClient;

import java.io.*;
import java.util.Properties;

public class StorageClient extends AbstractSSLClient {

	public StorageClient(String targetHost, int targetPort, Properties properties) throws Exception {
		super(targetHost, targetPort, properties);
	}

	public byte[] getFile(String filePath) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.println("GET " + filePath + " HTTP/1.0");
		out.println();
		out.flush();

		if (out.checkError()) {
			System.out.println("SSLSocketClient: java.io.PrintWriter error");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		if (!in.readLine().endsWith("OK")) {
			return new byte[]{};
		}
		in.readLine();
		in.readLine();
		in.readLine();

		String inputLine;
		StringBuilder fileContent = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			fileContent.append(inputLine).append("\n");
		}

		in.close();
		out.close();

		return fileContent.toString().getBytes();
	}

}
