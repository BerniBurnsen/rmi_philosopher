package edu.hm.vss.client;

import edu.hm.vss.helper.Logger;
import edu.hm.vss.interfaces.IClientToServer;
import edu.hm.vss.interfaces.Settings;

import java.rmi.AlreadyBoundException;
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
    private static Registry registry;
    private static List<IClientToServer> servers = new ArrayList<>();
    private static final int instanceCount = 2;
    private static final Logger logger = Logger.getInstance();

    public static final Map<Integer, Integer> allEatCounts = new ConcurrentHashMap<>();
    public static final Map<Integer, Integer> locationMap = new ConcurrentHashMap<>();

    public static void startRegistry() throws RemoteException
    {
        logger.printLog(Client.class.getName(), "StartRegistry");
        registry = LocateRegistry.createRegistry(Settings.PORT_CLIENT);
    }

    public static void registerObject(String name, Remote remoteObject) throws RemoteException, AlreadyBoundException
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

        logger.printLog(Client.class.getSimpleName(), "main #phil" + numberOfPhilosophers + " #hungry " + numberOfHungryPhilosophers + " #places " + numberOfPlaces);

        startRegistry();
        registerObject(Settings.SERVER_TO_CLIENT, new ServerToClient());

        logger.printLog(Client.class.getSimpleName(), "build up connections");
        //build up connections
        for(int i = 0 ; i < instanceCount; i++)
        {


            registry = LocateRegistry.getRegistry(Settings.SERVERS[i %Settings.SERVERS.length], Settings.PORT_SERVER_BASE +i);
            IClientToServer serverAPI = (IClientToServer)registry.lookup(Settings.CLIENT_TO_SERVER);
            servers.add(serverAPI);

            String leftNeighbour = (i-1) < 0 ? Settings.SERVERS[instanceCount % Settings.SERVERS.length] : Settings.SERVERS[(i-1) % Settings.SERVERS.length];
            String rightNeighbour = (i+1) < instanceCount ? Settings.SERVERS[(i+1) %Settings.SERVERS.length] :Settings.SERVERS[instanceCount -1] ;

            int rightPort = (i+1) < instanceCount ? Settings.PORT_SERVER_BASE + i + 1 : Settings.PORT_SERVER_BASE;
            int leftPort = (i-1) < 0 ? Settings.PORT_SERVER_BASE + (instanceCount-1) : Settings.PORT_SERVER_BASE  + i - 1;

            logger.printLog(Client.class.getSimpleName(), "leftNeighbour: " + leftNeighbour + " leftPort: " + leftPort);
            logger.printLog(Client.class.getSimpleName(), "rightNeighbour: " + rightNeighbour + " rightPort" + rightPort);

            serverAPI.initConnections(Settings.CLIENT_IP,Settings.PORT_CLIENT,rightNeighbour,rightPort,leftNeighbour,leftPort);
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
            servers.get(nextServerIndex).createNewPhilosopher(i,i >= numberOfPhilosophers - numberOfHungryPhilosophers ? true : false);
        }

        Thread.sleep(5 * 60 * 1000);
    }
}
