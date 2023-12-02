package utils;

import java.io.*;
import java.util.Hashtable;

public class HttpParser {

	private String requestLine;
	private final Hashtable<String, String> headers;
	private final StringBuilder body;

	public HttpParser() {
		headers = new Hashtable<>();
		body = new StringBuilder();
	}

	public void parseRequest(BufferedReader in) {
		try {
			setRequestLine(in.readLine());

			String header;
			while (!(header = in.readLine()).isEmpty()) {
				appendHeaderParameter(header);
			}

			if (headers.isEmpty()) return;

			String bodyLine;
			while ((bodyLine = in.readLine()) != null) {
				appendBody(bodyLine);
			}
		} catch (IOException | HttpFormatException e) {
			e.printStackTrace();
		}
	}

	public String getRequestLine() {
		return requestLine;
	}

	private void setRequestLine(String requestLine) throws HttpFormatException {
		if (requestLine == null || requestLine.isEmpty()) {
			throw new HttpFormatException("Invalid Request-Line: " + requestLine);
		}
		this.requestLine = requestLine;
	}

	private void appendHeaderParameter(String header) throws HttpFormatException {
		int idx = header.indexOf(":");
		if (idx == -1) {
			throw new HttpFormatException("Invalid Header Parameter: " + header);
		}
		headers.put(header.substring(0, idx), header.substring(idx + 1));
	}

	public String getHeaderParameter(String headerName){
		return headers.get(headerName);
	}

	public String getBody() {
		return body.toString();
	}

	private void appendBody(String bodyLine) {
		body.append(bodyLine).append("\r\n");
	}

}
