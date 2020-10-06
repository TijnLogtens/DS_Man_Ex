import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Sink{

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException, InterruptedException {
        String messageQueueUrl = String.format("rmi://%s/MessageQueue",args[0]); //args[0] is address:port
        MessageQueue mq = (MessageQueue) Naming.lookup(messageQueueUrl);
        int id = mq.register();
        while (true){
            String msg = mq.get(id);
            if(msg != null) System.out.println(msg);
            Thread.sleep(1000);

        }
    }
}
