package fileTransfer.FTTCPClient;

import fileTransfer.Crypto;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Paths;


public class FTTCPClient {

	static final int BLOCKSIZE = 1024;

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("usage: java FTTCPClient <host> <port> <file path>");
			System.exit(0);
		}

		String server = args[0];
		int port = Integer.parseInt(args[1]);
		String filePath = args[2];

		System.out.println("Reading: " + filePath);
		FileInputStream fileInputStream = new FileInputStream(filePath);

		Socket socket = new Socket(server, port);
		OutputStream outputStream = socket.getOutputStream();

		String fileName = Paths.get(filePath).getFileName().toString();

		System.out.println("Sending File Name: " + fileName);
		outputStream.write(fileName.getBytes());
		outputStream.write(new byte[]{0});

		System.out.println("Sending encrypted blocks of size: " + BLOCKSIZE);
		int n;
		byte[] buf = new byte[BLOCKSIZE];
		while ((n = fileInputStream.read(buf)) > 0) {
			outputStream.write(Crypto.encrypt(buf), 0, n);
		}

		socket.close();
		fileInputStream.close();

		System.out.println("Transfer complete");
	}

}
