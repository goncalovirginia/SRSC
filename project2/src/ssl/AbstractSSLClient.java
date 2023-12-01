package ssl;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.security.KeyStore;
import java.util.Properties;

public abstract class AbstractSSLClient {

	protected final SSLSocket socket;

	protected AbstractSSLClient(String targetHost, int targetPort, Properties properties) throws Exception {
		String keyStorePath = properties.getProperty("keyStore");
		char[] keyStorePassword = properties.getProperty("keyStorePassword").toCharArray();

		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(keyStorePath), keyStorePassword);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, keyStorePassword);
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(kmf.getKeyManagers(), null, null);

		socket = (SSLSocket) ctx.getSocketFactory().createSocket(targetHost, targetPort);
		socket.startHandshake();
	}

	public void closeSocket() throws IOException {
		socket.close();
	}

}
