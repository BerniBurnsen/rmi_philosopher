package edu.hm.vss.server;

import edu.hm.vss.helper.Logger;
import edu.hm.vss.interfaces.*;
import edu.hm.vss.model.Plate;
import edu.hm.vss.model.TablePiece;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by B3rni on 13.05.2015.
 */
public class RMIServer
{
    public static Registry registry;
    public static int instanceNumber;

    public static IServerToClient clientAPI;
    public static IServerToServer leftServerAPI;
    public static IServerToServer rightServerAPI;

    public static TablePiece tablePiece;

    public static List<Plate> plates = new ArrayList<>();

    private final static Logger logger = Logger.getInstance();

    public static void startRegistry(int instanceNumber) throws RemoteException
    {
        logger.printLog(RMIServer.class.getName(),"startRegistry - " + (Settings.PORT_SERVER_BASE+instanceNumber));
        registry = LocateRegistry.createRegistry(Settings.PORT_SERVER_BASE +instanceNumber);
    }

    public static void registerObject(String name, Remote remoteObject) throws RemoteException, AlreadyBoundException
    {
        registry.bind(name, remoteObject);
        logger.printLog(RMIServer.class.getSimpleName(), "registerObject - registered " + name + " " + remoteObject.getClass().getName());

    }

    public static void main(String[] args) throws Exception
    {
        if(args.length == 1)
        {
            instanceNumber = Integer.parseInt(args[0]);
            logger.printLog(RMIServer.class.getName(),"Server start " + instanceNumber);
            startRegistry(instanceNumber);
            registerObject(Settings.CLIENT_TO_SERVER, new ClientToServer());
            registerObject(Settings.SERVER_TO_SERVER, new ServerToServer());

            //registerObject("Test", new Test());
            //initDiningPhilosophers();
            while(true)
            {
                Thread.sleep(60 * 5 * 1000);
                logger.printLog(RMIServer.class.getName(),"sleeping again!");
            }
        }

    }
}
