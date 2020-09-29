import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;

public class Sink{

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException, InterruptedException {
        String messageQueueUrl = String.format("rmi://%s/MessageQueue",args[0]); //args[0] is address:port
        MessageQueue mq = (MessageQueue) Naming.lookup(messageQueueUrl);
        int id = new Random().nextInt(1000);
        mq.register(id);
        String oldMsg = "";
        while (true){
            String msg = mq.get(id);
            if(msg != null && !msg.equals(oldMsg)) System.out.println(msg);
            oldMsg = msg;
            Thread.sleep(1000); // might lose some message

        }
    }
}
