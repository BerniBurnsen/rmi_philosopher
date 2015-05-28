package edu.hm.vss.client;

import edu.hm.vss.helper.Logger;
import edu.hm.vss.interfaces.IClientToServer;
import edu.hm.vss.interfaces.Settings;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by B3rni on 13.05.2015.
 */
public class Client
{
    private Registry registry;
    private List<IClientToServer> servers = new ArrayList<>();
    public final int instanceCount = 2;
    public Logger logger;

    private int numberOfPhilosophers;
    private int numberOfHungryPhilosophers;
    private int numberOfPlaces;


    public final Map<Integer, Integer> allEatCounts = new ConcurrentHashMap<>();
    public final Map<Integer, Integer> locationMap = new ConcurrentHashMap<>();

    public Client(int numberPhil, int numberHungyPhil, int numberPlaces)
    {
        numberOfPhilosophers = numberPhil;
        numberOfHungryPhilosophers = numberHungyPhil;
        numberOfPlaces = numberPlaces;
        logger = new Logger();
    }

    public Map<Integer, Integer> getAllEatCounts()
    {
        return allEatCounts;
    }

    public Map<Integer, Integer> getLocationMap()
    {
        return locationMap;
    }

    public int getInstanceCount()
    {
        return instanceCount;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public void init() throws RemoteException, AlreadyBoundException, NotBoundException
    {
        logger.printLog(Client.class.getSimpleName(), "main - #phil" + numberOfPhilosophers + " #hungry " + numberOfHungryPhilosophers + " #places " + numberOfPlaces);

        startRegistry();
        registerObject(Settings.SERVER_TO_CLIENT + i, new ServerToClient(this));

        logger.printLog(Client.class.getSimpleName(), "main - build up connections");

        //build up connections
        for(int i = 0 ; i < instanceCount; i++)
        {
            registry = LocateRegistry.getRegistry(Settings.SERVERS[i %Settings.SERVERS.length], Settings.PORT_SERVER_BASE +i);
            IClientToServer serverAPI = (IClientToServer)registry.lookup(Settings.CLIENT_TO_SERVER + i);
            servers.add(serverAPI);
            serverAPI.initClientConnection(Settings.CLIENT_IP, Settings.PORT_CLIENT);
        }

        for(int i = 0 ; i < instanceCount; i++)
        {
            String leftNeighbour = (i-1) < 0 ? Settings.SERVERS[Settings.SERVERS.length -1] : Settings.SERVERS[(i-1) % Settings.SERVERS.length];
            String rightNeighbour = (i+1) <= instanceCount ? Settings.SERVERS[(i+1) %Settings.SERVERS.length] :Settings.SERVERS[instanceCount -1] ;

            int rightPort = (i+1) < instanceCount ? Settings.PORT_SERVER_BASE + i + 1 : Settings.PORT_SERVER_BASE;
            int leftPort = (i-1) < 0 ? Settings.PORT_SERVER_BASE + (instanceCount-1) : Settings.PORT_SERVER_BASE  + i - 1;

            logger.printLog(Client.class.getSimpleName(), "main - Instancenumber " + i + " leftNeighbour: " + leftNeighbour + " leftPort: " + leftPort);
            logger.printLog(Client.class.getSimpleName(), "main - Instancenumber " + i + " rightNeighbour: " + rightNeighbour + " rightPort " + rightPort);

            servers.get(i).initServerConnections(rightNeighbour, rightPort, leftNeighbour,leftPort);
        }

        //build plates
        int[] counterArray = new int[instanceCount];
        for(int i = 0 ; i < numberOfPlaces ; i++)
        {
            counterArray[i % instanceCount]++;
        }
        int currentIndex = 0;
        for(int i = 0 ; i < instanceCount ; i++)
        {
            servers.get(i).initServer(counterArray[i],numberOfPlaces,currentIndex);
            currentIndex+= counterArray[i];
        }

        //spawn philosophers
        for(int i = 0 ; i < numberOfPhilosophers ; i++)
        {
            int nextServerIndex = i % instanceCount;
            logger.printLog(Client.class.getSimpleName(), "main - spawning Phil - " + i);
            servers.get(nextServerIndex).createNewPhilosopher(i, i >= numberOfPhilosophers - numberOfHungryPhilosophers ? true : false);
        }

        try
        {

            Thread.sleep(2 * 1000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        //start philosophers
        for(int i = 0 ; i < instanceCount ; i++)
        {
            servers.get(i).startPhilosophers();
        }


    }


    public void startRegistry() throws RemoteException
    {
        logger.printLog(Client.class.getName(), "StartRegistry");
        registry = LocateRegistry.createRegistry(Settings.PORT_CLIENT);
    }

    public void registerObject(String name, Remote remoteObject) throws RemoteException, AlreadyBoundException
    {
        logger.printLog(Client.class.getSimpleName(), "registerObject " + name + " " + remoteObject.getClass().getName());
        registry.bind(name, remoteObject);
        logger.printLog(Client.class.getSimpleName(), "registered" + name);

    }

    public static void main(String[] args) throws Exception
    {
        int numberOfPhilosophers = Integer.parseInt(args[0]);
        int numberOfHungryPhilosophers = Integer.parseInt(args[1]);
        int numberOfPlaces = Integer.parseInt(args[2]);

        new Client(numberOfPhilosophers,numberOfHungryPhilosophers,numberOfPlaces).init();
        Thread.sleep(5 * 60 * 1000);

    }
}
