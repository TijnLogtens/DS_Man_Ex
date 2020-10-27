package node;

import util.Message;
import util.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentMap;

public class ClientHandler implements Runnable {
    private Socket con;
    private ConcurrentMap<Integer, String> data;

    public ClientHandler(Socket con, ConcurrentMap<Integer, String> data) {
        this.con = con;
        this.data = data;
    }


    @Override
    public void run() {
        try {
            Message message = (Message) new ObjectInputStream(con.getInputStream()).readObject();
            if(message.messageType == MessageType.NEWNODE){
                Message newNodeM = new Message(MessageType.CHANGE, message.id, message.message);
                // send message to old prev node
                // send message back with ID of old prev node to node in message
                // change prev node to message info
            }
            else if(message.messageType == MessageType.CHANGE){
                // change next node to message info
            }
            else if(message.messageType == MessageType.PUT){
                data.put(message.id,message.message);
            }
            else if(message.messageType == MessageType.GET){
                String value = data.get(message.id);
                if(value == null){
                    //TODO Send to peer Server
                }
                message.message = value;
                new ObjectOutputStream(con.getOutputStream()).writeObject(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
