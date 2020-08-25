import java.net.*;

import java.io.*;

/**
 * Talker
 */
public class Talker {



    public static void main(String[] args){
        try{
            Console c = System.console();
            String s = c.readLine();
            int port = 1200;
            InetAddress ina = InetAddress.getByName(args[0]);
            DatagramSocket ds = new DatagramSocket();
            DatagramPacket p = new DatagramPacket(s.getBytes(),s.getBytes().length,ina,port);
            ds.send(p);
            ds.close();
        } catch(IOException e){
            System.err.println(e.toString());
            System.exit(1);
        }


    }
}
