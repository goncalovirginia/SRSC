package ssl;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractSSLServer {

	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	protected Properties properties;
	protected ServerSocket serverSocket;

	protected AbstractSSLServer(String propertiesFilePath) throws Exception {
		initProperties(propertiesFilePath);
		System.setProperty("javax.net.ssl.trustStore", properties.getProperty("trustStore"));
		initServerSocket();
	}

	private SSLServerSocketFactory getSSLServerSocketFactory() throws Exception {
		String keyStorePath = properties.getProperty("keyStore");
		char[] keyStorePassword = properties.getProperty("keyStorePassword").toCharArray();

		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new FileInputStream(keyStorePath), keyStorePassword);
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, keyStorePassword);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

		return sslContext.getServerSocketFactory();
	}

	private void initProperties(String propertiesFilePath) throws IOException {
		properties = new Properties();
		properties.load(new BufferedReader(new FileReader(propertiesFilePath)));
	}

	private void initServerSocket() throws Exception {
		SSLServerSocketFactory ssf = getSSLServerSocketFactory();
		SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(Integer.parseInt(properties.getProperty("port")));

		ss.setEnabledProtocols(new String[]{"TLSv1.2"});
		ss.setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_128_GCM_SHA256"});
		ss.setNeedClientAuth(Boolean.parseBoolean(properties.getProperty("authClients")));

		serverSocket = ss;
	}

	protected void run() {
		while (true) {
			try {
				Socket clientConnection = serverSocket.accept();
				threadPool.execute(() -> {
					processConnection(clientConnection);
					try {
						clientConnection.close();
					} catch (IOException ignored) {
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract void processConnection(Socket clientConnection);

}
