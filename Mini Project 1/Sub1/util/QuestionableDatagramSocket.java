package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

public class QuestionableDatagramSocket extends DatagramSocket {

    public QuestionableDatagramSocket(int port) throws SocketException {
        super(port);
    }

    /***
     *
     * @param packets
     * @return Number of send packets
     * @throws IOException
     */
    public int send(DatagramPacket[] packets) throws IOException {
        Random r = new Random();
        int sendMessagesCounter = 0;
        switch (r.nextInt(4)) {
            case 0: // discard
                boolean discard_first_msg = true;
                for (DatagramPacket p :
                        packets) {
                    if (discard_first_msg) discard_first_msg = false;
                    else if (r.nextInt(2) == 1) {
                        super.send(p);
                        sendMessagesCounter++;
                    }
                }
                break;
            case 1: // duplicate
                boolean duplicate_first_msg = true;
                for (DatagramPacket p :
                        packets) {
                    if (duplicate_first_msg) {
                        super.send(p);
                        duplicate_first_msg = false;
                        sendMessagesCounter++;
                    }
                    if (r.nextInt(2) == 1) {
                        super.send(p);
                        sendMessagesCounter++;
                    }
                    super.send(p);
                    sendMessagesCounter++;
                }
                break;
            case 2: // reorder
                List<DatagramPacket> packetslist = Arrays.asList(packets.clone());
                Collections.shuffle(packetslist);
                for (DatagramPacket p : packetslist) {
                    super.send(p);
                    sendMessagesCounter++;
                }
                break;
            case 3:
                for (DatagramPacket p : packets) {
                    super.send(p);
                    sendMessagesCounter++;
                }

        }
        return sendMessagesCounter;

    }
}
