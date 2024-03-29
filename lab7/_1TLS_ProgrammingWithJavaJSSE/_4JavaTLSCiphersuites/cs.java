package _1TLS_ProgrammingWithJavaJSSE._4JavaTLSCiphersuites;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class cs {
	public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {
		final String[] protocols = new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"};

		for (final String protocol : protocols) {
			final SSLContext sslContext = SSLContext.getInstance(protocol);
			sslContext.init(null, null, null);
			final List<String> defaultCipherSuites = Arrays
					.asList(sslContext.getDefaultSSLParameters().getCipherSuites());
			Collections.sort(defaultCipherSuites);
			final List<String> supportedCipherSuites = Arrays
					.asList(sslContext.getSupportedSSLParameters().getCipherSuites());
			Collections.sort(supportedCipherSuites);
			for (final String cn : defaultCipherSuites) {
				System.out.println(protocol + ",default," + cn);
			}
			for (final String cn : supportedCipherSuites) {
				System.out.println(protocol + ",supported," + cn);
			}
		}
	}
}
