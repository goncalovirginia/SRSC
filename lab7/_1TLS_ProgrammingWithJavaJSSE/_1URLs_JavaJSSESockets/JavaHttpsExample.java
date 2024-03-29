package _1TLS_ProgrammingWithJavaJSSE._1URLs_JavaJSSESockets;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class JavaHttpsExample {

	public static void main(String[] args)
			throws Exception {

		// Ex., String httpsURL = "https://www.bpinet.pt:443";
		if (args.length != 1) {

			System.err.println("Use: JavaHttpsExample <url>");
			System.err.println("Ex: JavaHttpsExample https://www.bpinet.pt\n");
			System.exit(1);
		}


		String httpsURL = args[0];
		URL myurl = new URL(httpsURL);
		HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
		InputStream ins = con.getInputStream();
		InputStreamReader isr = new InputStreamReader(ins);
		BufferedReader in = new BufferedReader(isr);

		String inputLine;

		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);

		in.close();

	}

}