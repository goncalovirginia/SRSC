package auth;

import ssl.AbstractSSLClient;

import java.util.Properties;

public class AuthClient extends AbstractSSLClient {

	public AuthClient(String targetHost, int targetPort, Properties properties) throws Exception {
		super(targetHost, targetPort, properties);
	}

}
