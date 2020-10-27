package client;

import util.Message;
import util.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GetClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        int key = Integer.parseInt(args[2]);
        Message m = new Message(MessageType.GET,key,"");
        Socket con = new Socket(ip, port);
        new ObjectOutputStream(con.getOutputStream()).writeObject(m);

    }
}
