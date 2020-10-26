import java.beans.PropertyChangeListener;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageQueue extends Remote {
    void put(String message) throws RemoteException;
    String get(int id) throws RemoteException;
    int register() throws RemoteException;
}
