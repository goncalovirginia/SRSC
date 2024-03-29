package storage;

import ssl.AbstractSSLClient;
import utils.HttpParser;

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

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		HttpParser httpParser = new HttpParser();
		httpParser.parse(in);

		if (!httpParser.getRequestLine().endsWith("OK")) {
			return new byte[]{};
		}

		return httpParser.getBody().getBytes();
	}

	public String postFile(HttpParser httpParser) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.print(httpParser.getFullRequest());
		out.flush();

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		return in.readLine();
	}
}
