package fileTransfer.FTTCPServer; /**
 * FTTCPServer - servidor em TCP para transferencia de ficheiros
 * para um cliente TCP.
 */

import fileTransfer.Crypto;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FTTCPServer {

    public static final int PORT = 8000;
    static final int BLOCKSIZE = 1024;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server ready at port: " + PORT);

        for (; ; ) {
            Socket clientSocket = serverSocket.accept();
            InputStream is = clientSocket.getInputStream();

            int n;
            byte[] buf = new byte[BLOCKSIZE];
            for (n = 0; n < BLOCKSIZE; n++) {
                int s = is.read();
                if (s != -1) buf[n] = (byte) s;
                else System.exit(1);
                if (buf[n] == 0) break;
            }

            String filename = new String(buf, 0, n);
            System.out.println("Receiving: " + filename);

            FileOutputStream fileOutputStream = new FileOutputStream(filename + ".out");

            while ((n = is.read(buf)) > 0) {
                fileOutputStream.write(Crypto.decrypt(buf), 0, n);
            }

            System.out.println("Transfer complete");

            clientSocket.close();
            fileOutputStream.close();
        }
    }
}
