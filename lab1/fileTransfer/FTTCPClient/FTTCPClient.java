package fileTransfer.FTTCPClient;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;


public class FTTCPClient {
	
	static final int BLOCKSIZE = 1024;
	
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("usage: java FTTCPClient host port filename");
			System.exit(0);
		}
		String server = args[0];
		int port = Integer.parseInt(args[1]);
		String filename = args[2];
		
		System.out.println("Sending: " + filename);
		// open file
		FileInputStream f = new FileInputStream(filename);
		
		// Cria uma conexao para o servidor 
		Socket socket = new Socket(server, port);
		// Obtem o canal de escrita associado ao socket.
		OutputStream os = socket.getOutputStream();
		
		os.write(filename.getBytes()); // envia nome do ficheiro
		
		os.write(new byte[]{0}); // envia separador
		
		int n;
		byte[] buf = new byte[BLOCKSIZE];
		while ((n = f.read(buf)) > 0)   // copia o ficheiro para o servidor
			os.write(buf, 0, n);
		
		// Fecha o socket, quebrando a ligacao com o servidor.
		// (como consequencia tambem e' feito os.close() )
		socket.close();
		f.close();
		
		System.out.println("Done");
	}
	
}
