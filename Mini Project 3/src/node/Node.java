package node;

import node.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Node {

    private static ConcurrentMap<Integer,String> data = new ConcurrentHashMap<>();
    private static int localport;
    public static String nextPeerServer;
    public static int nextPeerServerPort;
    public static String prevPeerServer;
    public static int prevPeerServerPort;

    public static void main(String[] args) throws IOException {
        connectionArgs(args);
        //TODO Node Setup
        ServerSocket serverSocket = new ServerSocket(localport);
        while (true){
            Socket con = serverSocket.accept();
            new Thread(new ClientHandler(con,data)).start();
        }
    }

    private static void connectionArgs(String[] args){
        if(args.length == 1){
            localport = Integer.parseInt(args[0]);
        } else{
            if(args.length == 3){
                localport = Integer.parseInt(args[0]);
                nextPeerServer = args[1];
                nextPeerServerPort = Integer.parseInt(args[1]);
            } else{
                throw new IllegalArgumentException("invalid amount of args");
            }
        }


    }
}
