package edu.hm.vss.client;

import edu.hm.vss.helper.LogLevel;
import edu.hm.vss.helper.Logger;
import edu.hm.vss.interfaces.IClientToServer;
import edu.hm.vss.interfaces.Settings;
import edu.hm.vss.model.Overseer;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by B3rni on 13.05.2015.
 */
public class Client implements Serializable
{
    private final LogLevel[] logLevels =
            {
                    LogLevel.PHIL,
                    LogLevel.CLIENT,
                    LogLevel.SERVER,
                    LogLevel.OVERSEER,
                    LogLevel.REMOTE,
                    LogLevel.INIT,
                    LogLevel.ERROR,
                    LogLevel.FALLBACK
            };

    private Registry registry;
    private List<IClientToServer> servers = new ArrayList<>();
    private int instanceCount = 2;
    private Logger logger;

    private int numberOfPhilosophers;
    private int numberOfHungryPhilosophers;
    private int numberOfPlaces;

    private Overseer overseer;

    private Map<Integer, Integer> allEatCounts = new ConcurrentHashMap<>();
    private Map<Integer, Integer> locationMap = new ConcurrentHashMap<>();
    private ServerToClient serverToClient;
    private Map<Integer, String> activeServers = new TreeMap<>();

    public Client()
    {

    }

    public Client(int numberPhil, int numberHungyPhil, int numberPlaces)
    {
        numberOfPhilosophers = numberPhil;
        numberOfHungryPhilosophers = numberHungyPhil;
        numberOfPlaces = numberPlaces;
        logger = new Logger(logLevels);
        for(int i = 0; i < instanceCount; i++)
        {
            int port;
            String ip;
            ip = Settings.SERVERS[i%Settings.SERVERS.length];
            port = Settings.PORT_SERVER_BASE + i;
            activeServers.put(port, ip);
        }
        System.out.println("Client constructor");
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

    public void init(boolean isFallback) throws RemoteException, AlreadyBoundException, NotBoundException
    {
        logger.printLog(LogLevel.INIT, Client.class.getSimpleName(), "main - #phil" + numberOfPhilosophers + " #hungry " + numberOfHungryPhilosophers + " #places " + numberOfPlaces);

        if(!isFallback)
        {
            startRegistry();
            serverToClient = new ServerToClient(this);
            registerObject(Settings.SERVER_TO_CLIENT, serverToClient);
        }

        logger.printLog(LogLevel.INIT, Client.class.getSimpleName(), "main - build up connections");

        //build up connections
        for(Map.Entry<Integer, String> connection : activeServers.entrySet())
        {
            registry = LocateRegistry.getRegistry(connection.getValue(), connection.getKey());
            IClientToServer serverAPI = (IClientToServer)registry.lookup(Settings.CLIENT_TO_SERVER + (connection.getKey() - Settings.PORT_SERVER_BASE));
            servers.add(serverAPI);
            serverAPI.initClientConnection(Settings.CLIENT_IP, Settings.PORT_CLIENT);

            /*registry = LocateRegistry.getRegistry(Settings.SERVERS[i %Settings.SERVERS.length], Settings.PORT_SERVER_BASE +i);
            IClientToServer serverAPI = (IClientToServer)registry.lookup(Settings.CLIENT_TO_SERVER + i);
            servers.add(serverAPI);
            serverAPI.initClientConnection(Settings.CLIENT_IP, Settings.PORT_CLIENT);*/
        }

        List<Integer> activePorts = new LinkedList<>(activeServers.keySet());
        Collections.sort(activePorts);

        int count = 0;
        for(Integer currentPort : activePorts)
        {
            int leftPort = activePorts.contains(currentPort - 1) ? (currentPort -1) : activePorts.get(activePorts.size()-1);
            int rightPort = activePorts.contains(currentPort + 1) ? (currentPort + 1) : activePorts.get(0);

            String leftNeighbour = activeServers.get(leftPort);
            String rightNeighbour = activeServers.get(rightPort);
/*
            String leftNeighbour = (i-1) < 0 ? Settings.SERVERS[Settings.SERVERS.length -1] : Settings.SERVERS[(i-1) % Settings.SERVERS.length];
            String rightNeighbour = (i+1) <= instanceCount ? Settings.SERVERS[(i+1) %Settings.SERVERS.length] :Settings.SERVERS[instanceCount -1] ;

            int rightPort = (i+1) < instanceCount ? Settings.PORT_SERVER_BASE + i + 1 : Settings.PORT_SERVER_BASE;
            int leftPort = (i-1) < 0 ? Settings.PORT_SERVER_BASE + (instanceCount-1) : Settings.PORT_SERVER_BASE  + i - 1;
*/
            logger.printLog(LogLevel.INIT, Client.class.getSimpleName(), "main - Instancenumber " + count + " leftNeighbour: " + leftNeighbour + " leftPort: " + leftPort);
            logger.printLog(LogLevel.INIT, Client.class.getSimpleName(), "main - Instancenumber " + count + " rightNeighbour: " + rightNeighbour + " rightPort " + rightPort);

            servers.get(count++).initServerConnections(rightNeighbour, rightPort, leftNeighbour,leftPort);
        }

        //build plates
        int[] counterArray = new int[activeServers.size()];
        for(int i = 0 ; i < numberOfPlaces ; i++)
        {
            counterArray[i % activeServers.size()]++;
        }
        int currentIndex = 0;
        for(int i = 0 ; i < activeServers.size() ; i++)
        {
            servers.get(i).initServer(counterArray[i],numberOfPlaces,currentIndex);
            currentIndex+= counterArray[i];
        }

        //spawn Overseer
        overseer = new Overseer(this, 10);
        overseer.start();

        //spawn philosophers
        for (int i = 0; i < numberOfPhilosophers; i++)
        {
            int nextServerIndex = i % activeServers.size();
            logger.printLog(LogLevel.INIT, Client.class.getSimpleName(), "main - spawning Phil - " + i);
            if(!isFallback)
            {
                servers.get(nextServerIndex).createNewPhilosopher(i, i >= numberOfPhilosophers - numberOfHungryPhilosophers ? true : false);
                allEatCounts.put(i, 0);
            }
            else
            {
                servers.get(nextServerIndex).respawnPhilosopher(i, i >= numberOfPhilosophers - numberOfHungryPhilosophers ? true : false, allEatCounts.get(i));
            }
        }

        try
        {

            Thread.sleep(500);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        //start philosophers
        for(int i = 0 ; i < instanceCount ; i++)
        {
            servers.get(i).startPhilosophers();
        }

        new Timer().schedule(new TimerTask() {
                 @Override
                 public void run()
                 {
                     for(int i = 0 ; i < instanceCount ; i++)
                     {
                         try
                         {
                             servers.get(i).stopServer();
                         } catch (RemoteException e)
                         {
                             //e.printStackTrace();
                         }
                         overseer.interrupt();
                     }
                 }
             }
        , 100*1000);
    }


    public void startRegistry() throws RemoteException
    {
        logger.printLog(LogLevel.INIT, Client.class.getName(), "StartRegistry");
        registry = LocateRegistry.createRegistry(Settings.PORT_CLIENT);
    }

    public void registerObject(String name, Remote remoteObject) throws RemoteException, AlreadyBoundException
    {
        logger.printLog(LogLevel.INIT, Client.class.getSimpleName(), "registerObject " + name + " " + remoteObject.getClass().getName());
        registry.bind(name, remoteObject);
        logger.printLog(LogLevel.INIT, Client.class.getSimpleName(), "registered" + name);

    }

    public static void main(String[] args) throws Exception
    {
        int numberOfPhilosophers = Integer.parseInt(args[0]);
        int numberOfHungryPhilosophers = Integer.parseInt(args[1]);
        int numberOfPlaces = Integer.parseInt(args[2]);

        new Client(numberOfPhilosophers,numberOfHungryPhilosophers,numberOfPlaces).init(false);
    }

    public void startFallback(String ip)
    {
        //find out the missing server
        int missingPort = -1;
        Set<Integer> activePorts = activeServers.keySet();
        overseer.interrupt();

        for(int port : activePorts)
        {
            try
            {
                logger.printLog(LogLevel.ERROR, Client.class.getSimpleName(), "Search unreachable server or stop: " + activeServers.get(port) + " - " + port);
                servers.get(port - Settings.PORT_SERVER_BASE).stopServer();
            } catch (RemoteException e)
            {
                logger.printLog(LogLevel.ERROR, Client.class.getSimpleName(), "!!! --- Unreachable Server found: " + activeServers.get(port) + " - " + port);
                missingPort = port;
            }
        }
        if(missingPort > -1)
        {
            activeServers.remove(missingPort);
            instanceCount--;
        }
        if(instanceCount == 0)
        {
            logger.printLog(LogLevel.ERROR, Client.class.getSimpleName(), "!!! --- NO SERVERS FOUND --- !!!");
            logger.printLog(LogLevel.ERROR, Client.class.getSimpleName(), "Lost connection to all servers. Could not run fallback");
            logger.printLog(LogLevel.ERROR, Client.class.getSimpleName(), "!!! --- NO SERVERS FOUND --- !!!");
            return;
        }
        try
        {
            init(true);
        } catch (RemoteException e)
        {
            e.printStackTrace();
        } catch (AlreadyBoundException e)
        {
            e.printStackTrace();
        } catch (NotBoundException e)
        {
            e.printStackTrace();
        }
    }


    public Registry getRegistry()
    {
        return registry;
    }

    public void setRegistry(Registry registry)
    {
        this.registry = registry;
    }

    public List<IClientToServer> getServers()
    {
        return servers;
    }

    public void setServers(List<IClientToServer> servers)
    {
        this.servers = servers;
    }

    public void setInstanceCount(int instanceCount)
    {
        this.instanceCount = instanceCount;
    }

    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    public int getNumberOfPhilosophers()
    {
        return numberOfPhilosophers;
    }

    public void setNumberOfPhilosophers(int numberOfPhilosophers)
    {
        this.numberOfPhilosophers = numberOfPhilosophers;
    }

    public int getNumberOfHungryPhilosophers()
    {
        return numberOfHungryPhilosophers;
    }

    public void setNumberOfHungryPhilosophers(int numberOfHungryPhilosophers)
    {
        this.numberOfHungryPhilosophers = numberOfHungryPhilosophers;
    }

    public int getNumberOfPlaces()
    {
        return numberOfPlaces;
    }

    public void setNumberOfPlaces(int numberOfPlaces)
    {
        this.numberOfPlaces = numberOfPlaces;
    }

    public void setAllEatCounts(Map<Integer, Integer> allEatCounts)
    {
        this.allEatCounts = allEatCounts;
    }

    public void setLocationMap(Map<Integer, Integer> locationMap)
    {
        this.locationMap = locationMap;
    }

    public ServerToClient getServerToClient()
    {
        return serverToClient;
    }
}
