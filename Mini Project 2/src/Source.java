import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Source {

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        String messageQueueUrl = String.format("rmi://%s/MessageQueue",args[0]); //args[0] is address:port
        MessageQueue mq = (MessageQueue) Naming.lookup(messageQueueUrl);
        System.out.println("Enter message to send:");
        Scanner in = new Scanner(System.in);
        while (true){
            System.out.print(">> ");
            String s = in.nextLine();
            if(s.equals("q")) break;
            mq.put(String.format("(%s) -> %s",args[1],s));
        }
    }
}
