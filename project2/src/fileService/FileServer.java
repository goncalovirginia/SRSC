package fileService;

import ssl.AbstractSSLServer;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/* StorageServer.java --  a simple HTTP GET request file
 * supporting HTTP or HTTP/SSL (one-way or mutual authentication)
 */

public class FileServer extends AbstractSSLServer {

	private final String filesRootPath;

	public FileServer(ServerSocket ss, String filesRootPath) throws IOException {
		super(ss, filesRootPath);
		this.filesRootPath = filesRootPath;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java StorageServer <properties file path>");
		}

		try {
			// Create a Socket Factory of type mentioned
			// Plain Socket ... or SSL Socket and creates
			// the server socket in the specified port

			// See the getServerSocketFactory code below

			ServerSocketFactory ssf = FileServer.getServerSocketFactory(Boolean.parseBoolean(args[2]));
			ServerSocket ss = ssf.createServerSocket(4000);


			// This server only enables TLSv1.2
			// and the cipher suite below
			((SSLServerSocket) ss).setEnabledProtocols(new String[]{"TLSv1.2"});
			((SSLServerSocket) ss).setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_128_GCM_SHA256"});

			if (args.length >= 4 && args[3].equals("true")) {

				// In this case, mutual authentication will be used
				// so, client authentication will be required

				((SSLServerSocket) ss).setNeedClientAuth(true);

			}
			new FileServer(ss, "docroot");
		} catch (IOException e) {
			System.out.println("Problem with sockets: unable to start AbstractServer: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected void processConnection(Socket clientConnection) {

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
