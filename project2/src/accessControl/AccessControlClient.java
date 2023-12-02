package accessControl;

import ssl.AbstractSSLClient;

import java.util.Properties;

public class AccessControlClient extends AbstractSSLClient {

	public AccessControlClient(String targetHost, int targetPort, Properties properties) throws Exception {
		super(targetHost, targetPort, properties);
	}

}
