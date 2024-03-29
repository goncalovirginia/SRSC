package _1TLS_ProgrammingWithJavaJSSE._2TLS_CODE_JavaJSSE;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;


public class TLSClient {

	static {
		System.setProperty("javax.net.ssl.trustStore", "lab7/_1TLS_ProgrammingWithJavaJSSE/_2TLS_CODE_JavaJSSE/clienttruststore");
	}

	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(System.in));
		PrintStream out = System.out;

		SSLSocketFactory f =
				(SSLSocketFactory) SSLSocketFactory.getDefault();

		// Want to know the default enabled ciphersuites ...
		System.out.println("-----------------------------");
		System.out.println("Default Enabled Ciphersuites");
		System.out.println("-----------------------------");
		String[] defaultcsuites = f.getDefaultCipherSuites();
		for (int i = 0; i < defaultcsuites.length; i++)
			System.out.println(defaultcsuites[i]);

		// Want to know the total supported ciphersuites in your java runtime ...
		System.out.println("-----------------------------");
		System.out.println("All Supported Ciphersuites");
		System.out.println("-----------------------------");
		String[] supcsuites = f.getSupportedCipherSuites();
		for (int i = 0; i < supcsuites.length; i++)
			System.out.println(supcsuites[i]);

		try {
			SSLSocket c = (SSLSocket) f.createSocket(args[0], Integer.parseInt(args[1]));

			c.startHandshake();

			SSLSession session = c.getSession();

			System.out.println();
			System.out.println("Hum from my offer server decided to select\n");
			System.out.println("TLS protocol version: " + session.getProtocol());
			System.out.println("Ciphersuite: " + session.getCipherSuite());
			System.out.println();

			BufferedWriter w = new BufferedWriter(
					new OutputStreamWriter(c.getOutputStream()));
			BufferedReader r = new BufferedReader(
					new InputStreamReader(c.getInputStream()));
			String m = null;
			while ((m = r.readLine()) != "!quit") {
				out.println(m);
				m = in.readLine();
				System.out.println("input:" + m);

				w.write(m, 0, m.length());
				w.newLine();
				w.flush();
			}
			w.close();
			r.close();
			c.close();
		} catch (IOException e) {
			System.err.println(e.toString());
		}

	}
}




