package client;

import util.Message;
import util.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PutClient {

    public static void main(String[] args) throws IOException {
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        int key = Integer.parseInt(args[2]);
        String value = args[3];
        Message m = new Message(MessageType.PUT,key,value);
        Socket con = new Socket(ip, port);
        new ObjectOutputStream(con.getOutputStream()).writeObject(m);
    }
}

