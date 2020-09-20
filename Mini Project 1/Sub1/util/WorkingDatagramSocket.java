package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class WorkingDatagramSocket extends DatagramSocket {

    public WorkingDatagramSocket(int port) throws SocketException {
        super(port);
    }

    public int send(DatagramPacket[] packets) throws IOException{
        int counter = 0;
        for (DatagramPacket p : packets) {
            super.send(p);
            counter++;
        }
        return counter;
    }
}
