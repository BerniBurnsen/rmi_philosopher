package edu.hm.vss.server;

import edu.hm.vss.helper.Logger;
import edu.hm.vss.interfaces.*;
import edu.hm.vss.model.Plate;
import edu.hm.vss.model.TablePiece;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by B3rni on 13.05.2015.
 */
public class RMIServer implements Serializable
{
    private Registry registry;
    private final int instanceNumber;

    private IServerToClient clientAPI;
    private IServerToServer leftServerAPI;
    private IServerToServer rightServerAPI;

    private TablePiece tablePiece;

    private List<Plate> plates = new ArrayList<>();

    RMIServer(int instanceNumber)
    {
        this.instanceNumber = instanceNumber;
        System.out.println("RMIServer contructor");
    }

    public void init()throws AlreadyBoundException, RemoteException
    {
        startRegistry(instanceNumber);
        registerObject(Settings.CLIENT_TO_SERVER + instanceNumber, new ClientToServer(this));
        registerObject(Settings.SERVER_TO_SERVER + instanceNumber, new ServerToServer(this));
        System.out.println("RMIServer initialized, refid: " + this);
    }

    public void startRegistry(int instanceNumber) throws RemoteException
    {
        registry = LocateRegistry.createRegistry(Settings.PORT_SERVER_BASE +instanceNumber);
    }

    public void registerObject(String name, Remote remoteObject) throws RemoteException, AlreadyBoundException
    {
        registry.bind(name, remoteObject);
        //logger.printLog(RMIServer.class.getSimpleName(), "registerObject - registered " + name + " " + remoteObject.getClass().getName());

    }

    public static void main(String[] args) throws Exception
    {
        if(args.length == 1)
        {
            new RMIServer(Integer.parseInt(args[0])).init();
            while(true)
            {
                Thread.sleep(60*5*1000);
                //logger.printLog(RMIServer.class.getName(),"sleeping again!");
            }
        }
    }

    public void setTablePiece(TablePiece tablePiece)
    {
        this.tablePiece = tablePiece;
    }

    public void setLeftServerAPI(IServerToServer leftServerAPI)
    {
        this.leftServerAPI = leftServerAPI;
    }

    public void setRightServerAPI(IServerToServer rightServerAPI)
    {
        this.rightServerAPI = rightServerAPI;
    }

    public void setClientAPI(IServerToClient clientAPI)
    {
        this.clientAPI = clientAPI;
    }

    public IServerToClient getClientAPI()
    {

        return clientAPI;
    }

    public IServerToServer getLeftServerAPI()
    {
        return leftServerAPI;
    }

    public IServerToServer getRightServerAPI()
    {
        return rightServerAPI;
    }

    public TablePiece getTablePiece()
    {
        return tablePiece;
    }

    public List<Plate> getPlates()
    {
        return plates;
    }

    public int getInstanceNumber()
    {

        return instanceNumber;
    }

    public void setPlates(List<Plate> plates)
    {
        this.plates = plates;
    }
}
