import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;


public class MessageQueueImpl implements MessageQueue {

    private static int counter = 0;

    private static final int RMI_PORT = 1099;

    private Map<Integer, LinkedList<String>> sinks = new HashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void put(String message) throws RemoteException {
        System.out.println(message);
        lock.lock();
        for (int key: sinks.keySet()) {
            sinks.get(key).add(message);
        }
        lock.unlock();
    }

    @Override
    public String get(int id) throws RemoteException {
        try{
            return sinks.get(id).removeFirst();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    @Override
    public int register() throws RemoteException {
        lock.lock();
        counter++;
        sinks.put(counter,new LinkedList<>());
        lock.unlock();
        return counter;

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
