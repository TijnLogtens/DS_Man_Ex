import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import node.Node;
import util.Message;
import util.MessageType;

public class Test{

    public static void main(String[] args){
        exampleTest();
    }
    public static void ownTest(){
        try{
            Node n1 = new Node(new String[]{"localhost", "8000"});
            Node n2 = new Node(new String[]{"localhost", "8001", "localhost", "8000"});
            Node n3 = new Node(new String[]{"localhost", "8002", "localhost", "8001"});
            Node n4 = new Node(new String[]{"localhost", "8003", "localhost", "8002"});
            Node n5 = new Node(new String[]{"localhost", "8004", "localhost", "8003"});
    
            Node[] nodes = {n1, n2, n3, n4, n5};
    
            for(Node n: nodes){
                new Thread(){
                    public void run(){
                        try {
                            n.start();
                        } catch (Exception e) {
                            System.out.println("Bruh");
                        }
                    }
                  }.start();
            }

            for(int i=0; i<nodes.length-1; i++){
                assert(nodes[i].nextPeerServer == nodes[i+1].prevPeerServer);
                assert(nodes[i].nextPeerServerPort == nodes[i+1].prevPeerServerPort);
            }
            assert(nodes[nodes.length-1].nextPeerServer == nodes[0].prevPeerServer);
            assert(nodes[nodes.length-1].nextPeerServerPort == nodes[0].prevPeerServerPort);
            
            System.out.println("Nodes initialized, everything worked. Congrats.");

        }
        catch(Exception e){
            System.out.println("Something went wrong you idiot");
            e.printStackTrace();
        }
        
    }
    private static void startNode(Node n){
        new Thread(){
            public void run(){
                try {
                    n.start();
                } catch (Exception e) {
                    System.out.println("Bruh");
                }
            }
          }.start();
    }
    public static void exampleTest(){
        try{
            // Node starts at 1025.
            Node n1 = new Node(new String[]{"localhost", "1025"});
            startNode(n1);

            // Node starts at 1026, knowing 1025. 
            Node n2 = new Node(new String[]{"localhost", "1026", "localhost", "1025"});
            startNode(n2);
            
            // Client PUT(1, A) to 1025.
            Message m = new Message(MessageType.PUT,1,"A");
            Socket con = new Socket("localhost", 1025);
            new ObjectOutputStream(con.getOutputStream()).writeObject(m);

            // Client at 2048 sends GET(1, 2048) to 1025; 
            m = new Message(MessageType.GET,1,"");
            con = new Socket("localhost", 1025);

            //eventually receives PUT(1, A) at 2048 from someone.
            Message receivedM = (Message) new ObjectInputStream(con.getInputStream()).readObject();;
            assert(receivedM.message == "A");

            // Client at 2049 sends GET(1, 2049) to 1026; 
            m = new Message(MessageType.GET,1,"");
            con = new Socket("localhost", 1026);

            // eventually receives PUT(1, A) at 2049 from someone.
            receivedM = (Message) new ObjectInputStream(con.getInputStream()).readObject();;
            assert(receivedM.message == "A");

            // Client at 2049 sends GET(2, 2049) to 1025;
            m = new Message(MessageType.GET,2,"");
            con = new Socket("localhost", 1025); 

            // eventually receives message saying the resource is not found.
            receivedM = (Message) new ObjectInputStream(con.getInputStream()).readObject();;
            assert(receivedM.message == "RESOURCE NOT FOUND");

            // Node C starts at 1027, knowing 1026.
            Node n3 = new Node(new String[]{"localhost", "1027", "localhost", "1026"});
            startNode(n3);

            // Client at 2048 sends GET(1, 2048) to 1027; 
            m = new Message(MessageType.GET,1,"");
            con = new Socket("localhost", 1027); 

            // eventually receives PUT(1, A) at 2048 from someone.
            receivedM = (Message) new ObjectInputStream(con.getInputStream()).readObject();;
            assert(receivedM.message == "A");

            System.out.println("EVERYTHING WORKED!!! YAY! PARTY TIME");
        }
        catch(Exception e){
            System.out.println("Something went wrong you idiot");
            e.printStackTrace();
        }
        
    }
}