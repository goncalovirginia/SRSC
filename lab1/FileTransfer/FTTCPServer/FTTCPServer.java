
/**
 * FTTCPServer - servidor em TCP para transferencia de ficheiros 
 * para um cliente TCP.
 */

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FTTCPServer {

        static final int BLOCKSIZE = 1024;
	public static final int PORT = 8000 ;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ServerSocket serverSocket = new ServerSocket( PORT ) ;

		for(;;) {
			System.out.println("Server ready at port "+PORT);

			Socket clientSocket = serverSocket.accept() ;
			InputStream is = clientSocket.getInputStream();

			int n ;
			byte[] buf = new byte[BLOCKSIZE] ;
			
			for ( n=0; n<BLOCKSIZE; n++ ) {  
			    // file name sent by client
				int s = is.read();
				if ( s!=-1 ) buf[n]=(byte)s;
				else System.exit(1);
				if ( buf[n] == 0 ) break;
			}
			String filename= new String(buf,0,n);
			System.out.println("Receiving: '"+new String(buf, 0, n)+"'");
			System.out.println("Receiving: "+filename);	

			FileOutputStream f = new FileOutputStream(new String(buf,0,n));
  		       // FileOutputStream f = new FileOutputStream("tmp.out");

			// Instrumentation for transfer statstics
                        long startime=System.currentTimeMillis();
			int count=0;
			while( (n = is.read( buf )) > 0 ) 
			    {
			    // write the received blocks
			        count=count+n;
				f.write( buf, 0, n ) ;
			    }

			// Instrumentation for transfer statstics
                        long endtime=System.currentTimeMillis();

			clientSocket.close();
			// close file
			f.close();

			// Transfer statistics observed by server
                        count=8*count/1000;
                        System.out.println("Throughput: " + count/(endtime-startime) + " Kbits/s" );

		}
	}
}
