package utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpParser {

	private String requestLine;
	private final Map<String, String> headers;
	private final StringBuilder body;

	public HttpParser() {
		headers = new HashMap<>();
		body = new StringBuilder();
	}

	public void parse(BufferedReader in) {
		try {
			setRequestLine(in.readLine());

			String headerParameter;
			while (!(headerParameter = in.readLine()).isEmpty()) {
				appendHeaderParameter(headerParameter);
			}

			if (headers.get("Content-Length") == null) return;

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

	public String getAllHeaderParameters() {
		StringBuilder stringBuilder = new StringBuilder();

		for (Map.Entry<String, String> header : headers.entrySet()) {
			stringBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
		}

		return stringBuilder.toString();
	}

	public String getBody() {
		return body.toString();
	}

	private void appendBody(String bodyLine) {
		body.append(bodyLine).append("\r\n");
	}

	public String getFullRequest() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(requestLine).append("\r\n");

		for (Map.Entry<String, String> header : headers.entrySet()) {
			stringBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
		}

		stringBuilder.append("\r\n").append(body);

		return stringBuilder.toString();
	}

}
