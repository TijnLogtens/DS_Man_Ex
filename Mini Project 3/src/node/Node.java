package node;

import util.Message;
import util.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.io.ObjectOutputStream;

public class Node {

    protected ConcurrentMap<Integer,String> data = new ConcurrentHashMap<>();
    private String localServer;
    private int localPort;
    public String nextPeerServer;
    public int nextPeerServerPort;
    public String prevPeerServer;
    public int prevPeerServerPort;

    public static void main(String[] args) throws IOException {
        Node n = new Node(args);
        n.start();
    }

    public Node(String[] args) {
        connectionArgs(args);
    }

    public void start() throws IOException {
        notifyPear();
        ServerSocket serverSocket = new ServerSocket(this.localPort);
        while (true){
            Socket con = serverSocket.accept();
            new Thread(new ClientHandler(this, con)).start();
        }
    }
    private void connectionArgs(String[] args){
        if(args.length == 2){
            localServer = args[0];
            localPort = Integer.parseInt(args[1]);
            nextPeerServer = localServer;
            nextPeerServerPort = localPort;
            prevPeerServer = localServer;
            prevPeerServerPort = localPort;
        } else{
            if(args.length == 4){
                localServer = args[0];
                localPort = Integer.parseInt(args[1]);
                nextPeerServer = args[2];
                nextPeerServerPort = Integer.parseInt(args[3]);
            } else{
                throw new IllegalArgumentException("invalid amount of args");
            }
        }
        
    }

    private void notifyPear(){
        try{
            Message m = new Message(MessageType.NEWNODE, localPort, localServer);
            Socket con = new Socket(nextPeerServer, nextPeerServerPort);
            new ObjectOutputStream(con.getOutputStream()).writeObject(m);
        }
        catch(Exception e){
            //asdf
        }
        
    }
    @Override
    public String toString() {
        return localServer+":"+localPort;
    }
}
