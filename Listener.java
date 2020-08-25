import java.io.IOException;
import java.net.*;


public class Listener{
    public static void main(String[] args){
        try{
            int port = 1200;
            byte[] buf = new byte[256];
            DatagramSocket sc = new DatagramSocket(port);
            while(true){
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                sc.receive(packet);
                String str = new String(buf);
                System.out.println(packet.getSocketAddress() + " " + str);
                buf = new byte[256];
            }
        }catch(IOException e){
            System.err.println(e.toString());
            System.exit(1);
        }
        
    }
}
