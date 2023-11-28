package _1TLS_ProgrammingWithJavaJSSE._2TLS_CODE_JavaJSSE;

import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class PrintCert {
	public static void main(String args[]) {
		try {
			FileInputStream fr = new FileInputStream(args[0]);
			CertificateFactory cf =
					CertificateFactory.getInstance("X509");

			X509Certificate c = (X509Certificate) cf.generateCertificate(fr);
			System.out.println("Informacao de atributos no certificado:");
			System.out.println("---------------------------------------------");
			System.out.println("Version Nr. :\n" + c.getVersion());
			System.out.println("Serial Nr. :\n" + c.getSerialNumber());

			System.out.println("Subject (issued to):\n"
					+ c.getSubjectDN());
			System.out.println("Issuer (issued by) :\n"
					+ c.getSubjectDN());
			System.out.println("Valid from:\n"
					+ c.getNotBefore());
			System.out.println("Valid to: \n" + c.getNotAfter());

			System.out.println("Signature Alg :\n" + c.getSigAlgName());
			System.out.println("Signature Alg OID :\n" + c.getSigAlgOID());


			System.out.println("---------------------------------------------");
			System.out.println("Aproveite para ver todos os outros metodos");
			System.out.println("na classe X509Certificate...");

			System.out.println("---------------------------------------------");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


	    
