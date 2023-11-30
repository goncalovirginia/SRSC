package _1TLS_ProgrammingWithJavaJSSE._3AnotherTLS_EXAMPLE.client_server.server;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.security.KeyStore;

/* StorageServer.java --  a simple HTTP GET request file
 * supporting HTTP or HTTP/SSL (one-way or mutual authentication)
 */

public class ClassFileServer extends ClassServer {

	private String filesRootPath;

	public ClassFileServer(ServerSocket ss, String filesRootPath) throws IOException {
		super(ss);
		this.filesRootPath = filesRootPath;
	}

	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("Usage: java StorageServer <port> <filesroot> <true/false (use TLS)> <true/false (authenticate clients)>");
		}

		try {
			// Create a Socket Factory of type mentioned
			// Plain Socket ... or SSL Socket and creates
			// the server socket in the specified port

			// See the getServerSocketFactory code below

			ServerSocketFactory ssf = ClassFileServer.getServerSocketFactory(Boolean.parseBoolean(args[2]));
			ServerSocket ss = ssf.createServerSocket(4001);


			// This server only enables TLSv1.2
			// and the cipher suite below
			((SSLServerSocket) ss).setEnabledProtocols(new String[]{"TLSv1.2"});
			((SSLServerSocket) ss).setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_128_GCM_SHA256"});

			if (args.length >= 4 && args[3].equals("true")) {

				// In this case, mutual authentication will be used
				// so, client authentication will be required

				((SSLServerSocket) ss).setNeedClientAuth(true);

			}
			new ClassFileServer(ss, "docroot");
		} catch (IOException e) {
			System.out.println("Problem with sockets: unable to start AbstractServer: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static ServerSocketFactory getServerSocketFactory(boolean useTLS) {
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

	/**
	 * getBytes() returns the file as an array of bytes
	 * the file name is represented by the argument <b>path</b>.
	 *
	 * @return the bytes for the file
	 * @throws FileNotFoundException if the file is not found in
	 *                               <b>path</b> and can not be loaded or readable(ex., permission problems).
	 */
	public byte[] getBytes(String path)
			throws IOException {
		System.out.println("file asked by the client to send: " + path);
		File f = new File(filesRootPath + File.separator + path);
		int length = (int) (f.length());
		if (length == 0) {
			throw new IOException("File length is zero: " + path);
		} else {
			FileInputStream fin = new FileInputStream(f);
			DataInputStream in = new DataInputStream(fin);

			byte[] bytecodes = new byte[length];
			in.readFully(bytecodes);
			return bytecodes;
		}
	}
}
