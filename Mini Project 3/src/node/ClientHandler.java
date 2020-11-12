package node;

import util.Message;
import util.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {
    private final Socket con;
    private final Node node;

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
                handleGet(message);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleGet(Message message) throws IOException, ClassNotFoundException {
        String value = node.data.get(message.id);
        if(message.message.equals(node.toString())){
            value = "RESOURCE NOT FOUND";
            message.message = value;
            new ObjectOutputStream(con.getOutputStream()).writeObject(message);
        }
        if(value == null){
            String sender = message.message;
            if(sender.equals("")){
                sender = node.toString();
            }
            Message m = new Message(MessageType.GET, message.id, sender);
            boolean success = handleNext(m);
            if(!success && m.message.equals(node.toString())){
                m.direction = 1;
                success = handleNext(m);
                if(!success && m.message.equals(node.toString())){
                    new ObjectOutputStream(con.getOutputStream()).writeObject("RESOURCE NOT FOUND");
                }
            }

        }
        else{ // I have the value
            message.message = value;
            new ObjectOutputStream(con.getOutputStream()).writeObject(message);
        }
    }

    private boolean handleNext(Message m) throws IOException, ClassNotFoundException {
            String server = m.direction == 0 ? node.nextPeerServer : node.prevPeerServer;
            int port = m.direction == 0 ? node.nextPeerServerPort : node.prevPeerServerPort;
            System.out.println(node.nextPeerServer +":"+ node.prevPeerServer);
            System.out.println(node.nextPeerServerPort  +":"+  node.prevPeerServerPort);
            try(Socket c = new Socket(server, port)){
                c.setSoTimeout(5000);
                new ObjectOutputStream(c.getOutputStream()).writeObject(m);
                Message nm = (Message) new ObjectInputStream(c.getInputStream()).readObject();
                new ObjectOutputStream(con.getOutputStream()).writeObject(nm);
                return true;
            } catch (SocketException e){
                if(!m.message.equals(node.toString())) con.close();
                return false;
            }
    }

}
