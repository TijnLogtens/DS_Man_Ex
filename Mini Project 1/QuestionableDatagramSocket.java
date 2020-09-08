import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

public class QuestionableDatagramSocket extends DatagramSocket {

    public QuestionableDatagramSocket(int port) throws SocketException {
        super(port);
    }

    public void send(DatagramPacket[] packets) throws IOException {
        Random r = new Random();
        switch (r.nextInt(3)) {
            case 0: // discard
                System.out.println("discard");
                boolean discard_first_msg = true;
                for (DatagramPacket p :
                        packets) {
                    if(discard_first_msg) discard_first_msg = false;
                    else if(r.nextInt(1) == 1)  super.send(p);
                }
                break;
            case 1: // duplicate
                System.out.println("duplicate");
                for (DatagramPacket p :
                        packets) {
                    boolean duplicate_first_msg = true;
                    if(duplicate_first_msg){
                        super.send(p);
                        duplicate_first_msg = false;
                    }
                    if(r.nextInt(1) == 1) super.send(p);
                    super.send(p);
                }
                break;
            case 3: // reorder
                System.out.println("normal or reorder");
                List<DatagramPacket> packetslist = Arrays.asList(packets.clone());
                Collections.shuffle(packetslist);
                for (DatagramPacket p: packetslist) {
                    super.send(p);
                }
        }

    }
}
