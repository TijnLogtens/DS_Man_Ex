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
    private Node node;

    public ClientHandler(Node n, Socket con) {
        this.con = con;
        this.node = n;
    }


    @Override
    public void run() {
        try {
            Message message = (Message) new ObjectInputStream(con.getInputStream()).readObject();
            if(message.messageType == MessageType.NEWNODE){
                // just got a message saying "Hey, you're my next node. Who's my previous node"

                // need to notify old previous node telling him that his next node has changed
                Message newNodeM = new Message(MessageType.CHANGENEXT, message.id, message.message);
                Socket c = new Socket(node.prevPeerServer, node.prevPeerServerPort);
                new ObjectOutputStream(c.getOutputStream()).writeObject(newNodeM);

                // send message back with ID of old previous node to node in message
                newNodeM = new Message(MessageType.CHANGEPREVIOUS, node.prevPeerServerPort, node.prevPeerServer);
                c = new Socket(message.message, message.id);
                new ObjectOutputStream(c.getOutputStream()).writeObject(newNodeM);

                // change next node to message info
                node.nextPeerServer = message.message;
                node.nextPeerServerPort = message.id;
            }
            else if(message.messageType == MessageType.CHANGEPREVIOUS){
                node.prevPeerServer = message.message;
                node.prevPeerServerPort = message.id;
            }
            else if(message.messageType == MessageType.CHANGENEXT){
                node.nextPeerServer = message.message;
                node.nextPeerServerPort = message.id;
            }
            else if(message.messageType == MessageType.PUT){
                node.data.put(message.id,message.message);
            }
            else if(message.messageType == MessageType.GET){
                String value = node.data.get(message.id);
                if(message.message == node.toString()){
                    value = "RESOURCE NOT FOUND";
                    message.message = value;
                    new ObjectOutputStream(con.getOutputStream()).writeObject(message);
                }
                if(value == null){
                    String sender = message.message;
                    if(sender == ""){
                        sender = node.toString();
                    }
                    Message m = new Message(MessageType.GET, message.id, sender);
                    Socket c = new Socket(node.nextPeerServer, node.nextPeerServerPort);
                    new ObjectOutputStream(con.getOutputStream()).writeObject(m);
                }
                else{
                    message.message = value;
                    new ObjectOutputStream(con.getOutputStream()).writeObject(message);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
