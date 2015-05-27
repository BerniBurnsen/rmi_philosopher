package edu.hm.vss.interfaces;

import edu.hm.vss.model.TablePiece;

import java.rmi.Remote;

/**
 * Created by B3rni on 20.05.2015.
 */
public interface IClientToServer extends Remote
{
    /**
     * Initialites all necessary connection from client to server and backwars additionally from server to the server neighbours.
     * @param ClientIP
     * @param ClientPort
     * @param rightNeighbourIP
     * @param rightNeighbourPort
     * @param leftNeighbourIP
     * @param leftNeighbourPort
     * @return
     */
    boolean initConnections(String ClientIP, int ClientPort, String rightNeighbourIP, int rightNeighbourPort, String leftNeighbourIP, int leftNeighbourPort);

    /**
     * initializes the Table, plates and forks on a server
     * @param seats
     * @param maxSeats
     * @param startIndex
     * @return
     */
    boolean initServer(int seats, int maxSeats, int startIndex);

    /**
     * Client spawns new philosophers on server
     * @param index
     * @param hungry
     * @return
     */
    boolean createNewPhilosopher(int index, boolean hungry);

    /**
     * Client respawns a philospher on server
     * @param index
     * @param hungry
     * @param eatCount
     * @return
     */
    boolean respawnPhilosopher(int index, boolean hungry, int eatCount);

    /**
     * check if server is reachable
     * @return
     */
    boolean isReachable();
}
