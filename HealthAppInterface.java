import java.rmi.Remote;
import java.rmi.RemoteException;


//the unique functions that are provided by the system
//must be interfaced

public interface HealthAppInterface extends Remote {
    String register(String username, String password) throws RemoteException;
    String login(String username, String password) throws RemoteException;
    String provideInitialHealthInfo(String username, String healthInfo) throws RemoteException;
    String getTDEE(String username) throws RemoteException;
    String getBMI(String username) throws RemoteException;
    String timeToReachWeightGoal(String username) throws RemoteException;
}
