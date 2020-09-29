import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


public class MessageQueueImpl implements MessageQueue {

    private static final int RMI_PORT = 1099;

    private Map<Integer,String> sinks = new HashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void put(String message) throws RemoteException {
        System.out.println(message);
        lock.lock();
        for (int key: sinks.keySet()) {
            sinks.put(key,message);
        }
        lock.unlock();
    }

    @Override
    public synchronized String get(int id) throws RemoteException {
        return sinks.get(id);
    }

    @Override
    public void register(int id) throws RemoteException {
        sinks.put(id,null);
    }


    public static void main(String[] args) throws RemoteException, MalformedURLException {
        LocateRegistry.createRegistry(RMI_PORT);
        MessageQueue mq = new MessageQueueImpl();
        Remote mqStub = UnicastRemoteObject.exportObject(mq,0);
        String mqUrl = String.format("rmi://localhost:%s/MessageQueue",RMI_PORT);
        Naming.rebind(mqUrl,mqStub);
        System.out.println("MessageQueue available");
    }
}
