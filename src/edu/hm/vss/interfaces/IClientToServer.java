package edu.hm.vss.interfaces;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * interface for the client to server communication
 * It is used to control and initialize the server to get them ready to work.
 */
public interface IClientToServer extends Remote, Serializable
{
    boolean initClientConnection(String clientIP, int clientPort) throws RemoteException, NotBoundException;

    /**
     * Initialites all necessary connection from client to server and backwars additionally from server to the server neighbours.
     * @param rightNeighbourIP
     * @param rightNeighbourPort
     * @param leftNeighbourIP
     * @param leftNeighbourPort
     * @return
     */
    boolean initServerConnections(String rightNeighbourIP, int rightNeighbourPort, String leftNeighbourIP, int leftNeighbourPort) throws RemoteException, NotBoundException;

    /**
     * initializes the Table, plates and forks on a server
     * @param seats
     * @param maxSeats
     * @param startIndex
     * @return
     */
    boolean initServer(int seats, int maxSeats, int startIndex) throws RemoteException;

    /**
     * Client spawns new philosophers on server
     * @param index
     * @param hungry
     * @return
     */
    boolean createNewPhilosopher(int index, boolean hungry) throws RemoteException;

    void startPhilosophers() throws RemoteException;

    /**
     * Client respawns a philospher on server
     * @param index
     * @param hungry
     * @param eatCount
     * @return
     */
    boolean respawnPhilosopher(int index, boolean hungry, int eatCount) throws RemoteException;

    /**
     * stops the server and every philosopher on it
     */
    void stopServer() throws RemoteException;

    /**
     * punish philosopher
     */
    void punishPhilosopher(int index) throws RemoteException;
}
