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
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.println("POST /" + username + " HTTP/1.0");
		out.println("Operation: login");
		out.println("Content-Length: " + password.length());
		out.println();
		out.println(password);
		out.flush();

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		HttpParser httpParser = new HttpParser();
		httpParser.parse(in);

		return httpParser.getRequestLine().endsWith("OK");
	}

	public byte[] getFile(String username, String filePath) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.println("GET /" + username + "/" + filePath + " HTTP/1.0");
		out.println("Operation: getFile");
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

	public String postFile(String username, String filePath, byte[] fileBytes) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.println("POST /" + username + "/" + filePath + " HTTP/1.0");
		out.println("Operation: postFile");
		out.println("Content-Length: " + fileBytes.length);
		out.println("Content-Type: text/html");
		out.println();
		out.print(new String(fileBytes));
		out.flush();

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		HttpParser httpParser = new HttpParser();
		httpParser.parse(in);
		return httpParser.getRequestLine();
	}

	public String listFiles(String username, String path) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.println("GET /" + username + "/" + path + " HTTP/1.0");
		out.println("Operation: listFiles");
		out.println();
		out.flush();

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		HttpParser httpParser = new HttpParser();
		httpParser.parse(in);

		if (!httpParser.getRequestLine().endsWith("OK")) {
			return httpParser.getRequestLine();
		}

		return httpParser.getBody();
	}

	public String postDirectory(String username, String path) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.println("POST /" + username + "/" + path + " HTTP/1.0");
		out.println("Operation: postDirectory");
		out.println();
		out.flush();

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		HttpParser httpParser = new HttpParser();
		httpParser.parse(in);
		return httpParser.getRequestLine();
	}

	public String copyFile(String username, String path, String destinationPath) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.println("PUT /" + username + "/" + path + " HTTP/1.0");
		out.println("Operation: copyFile");
		out.println("Content-Length: " + destinationPath.length());
		out.println();
		out.println(destinationPath);
		out.flush();

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		HttpParser httpParser = new HttpParser();
		httpParser.parse(in);
		return httpParser.getRequestLine();
	}

	public String deleteFile(String username, String filePath) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.println("DELETE /" + username + "/" + filePath + " HTTP/1.0");
		out.println("Operation: deleteFile");
		out.println();
		out.flush();

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		HttpParser httpParser = new HttpParser();
		httpParser.parse(in);
		return httpParser.getRequestLine();
	}

	public String infoFile(String username, String filePath) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.println("GET /" + username + "/" + filePath + " HTTP/1.0");
		out.println("Operation: infoFile");
		out.println();
		out.flush();

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		HttpParser httpParser = new HttpParser();
		httpParser.parse(in);

		if (!httpParser.getRequestLine().endsWith("OK")) {
			return httpParser.getRequestLine();
		}

		return httpParser.getBody();
	}

}
