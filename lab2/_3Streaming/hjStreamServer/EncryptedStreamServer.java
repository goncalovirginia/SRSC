package _3Streaming.hjStreamServer;

import _3Streaming.Crypto;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

class EncryptedStreamServer {

    static public void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Erro, usar: mySend <movie> <ip-multicast-address> <port>");
            System.out.println("        or: mySend <movie> <ip-unicast-address> <port>");
            System.exit(-1);
        }

        int size;
        int count = 0;
        long time;
        DataInputStream g = new DataInputStream(new FileInputStream(args[0]));
        byte[] buff = new byte[4096];

        DatagramSocket s = new DatagramSocket();
        InetSocketAddress addr = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
        DatagramPacket p = new DatagramPacket(buff, buff.length, addr);
        long t0 = System.nanoTime(); // tempo de referencia para este processo
        long q0 = 0;

        while (g.available() > 0) {
            size = g.readShort();
            time = g.readLong();
            if (count == 0) q0 = time; // tempo de referencia no stream
            count += 1;
            g.readFully(buff, 0, size);
            p.setData(Crypto.encrypt(buff), 0, size);
            p.setSocketAddress(addr);
            long t = System.nanoTime();
            Thread.sleep(Math.max(0, ((time - q0) - (t - t0)) / 1000000));

            // send packet (with a frame payload)
            // Frames sent encrypted
            s.send(p);
            System.out.print(".");
        }

        System.out.println("\nDONE! all frames sent: " + count);
    }

}
