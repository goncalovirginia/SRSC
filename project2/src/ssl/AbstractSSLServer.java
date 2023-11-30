package ssl;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractSSLServer {

	protected static Properties properties;

	protected final ServerSocket serverSocket;
	private final ExecutorService threadPool = Executors.newCachedThreadPool();

	protected AbstractSSLServer(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public static void initProperties(String propertiesFilePath) throws IOException {
		properties = new Properties();
		properties.load(new BufferedReader(new FileReader(propertiesFilePath)));
	}

	protected static ServerSocket initServerSocket() throws IOException {
		ServerSocketFactory ssf = getServerSocketFactory(Boolean.parseBoolean(properties.getProperty("useTLS")));
		ServerSocket ss = ssf.createServerSocket(Integer.parseInt(properties.getProperty("port")));

		((SSLServerSocket) ss).setEnabledProtocols(new String[]{"TLSv1.2"});
		((SSLServerSocket) ss).setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_128_GCM_SHA256"});
		((SSLServerSocket) ss).setNeedClientAuth(Boolean.parseBoolean(properties.getProperty("authClients")));

		return ss;
	}

	protected static ServerSocketFactory getServerSocketFactory(boolean useTLS) {
		if (useTLS) {
			try {
				// set up key manager to do server authentication
				SSLContext ctx;
				KeyManagerFactory kmf;
				KeyStore ks;

				// Depending on the passphrase used to protect the
				// serverkeystore used. Remember, this is the keystore
				// where the server stores its keys as generate ...
				// See the keytool for details
				// The keystore created with the required password

				char[] kspwd = "hjhjhjhj".toCharArray();
				char[] ksepwd = "hjhjhjhj".toCharArray();

				ks = KeyStore.getInstance("JKS");
				ks.load(new FileInputStream("serverkeystore"), kspwd);
				kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, ksepwd);
				ctx = SSLContext.getInstance("TLS");
				ctx.init(kmf.getKeyManagers(), null, null);

				// only to deal with the keystore internal representation
				// you have KeyStore class with a lot of methods - see
				// the Java documentation.
				// Ex., KeyStore.getKey() takes a string corresponding to
				// an alias name and a char array representing a password
				// protecting the entry of the keystore, returning
				// java.security.Key object

				return ctx.getServerSocketFactory();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ServerSocketFactory.getDefault();
	}

	public void run() {
		while (true) {
			try {
				Socket clientConnection = serverSocket.accept();
				threadPool.execute(() -> processConnection(clientConnection));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract void processConnection(Socket clientConnection);

}
