package edu.hm.vss.userInterface;

import edu.hm.vss.helper.LogLevel;
import edu.hm.vss.interfaces.IServerToClient;
import edu.hm.vss.interfaces.Settings;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Created by Joncn on 03.06.2015.
 */
public class User
{

    public static void main(String... args)
    {
        try
        {
            Registry registry;
            registry = LocateRegistry.getRegistry(Settings.CLIENT_IP, Settings.PORT_CLIENT);
            IUserInterface userInterface = (IUserInterface) registry.lookup(Settings.USERINTERFACE);
            System.out.println("Client found");

            String input;
            while(true)
            {
                printUsage();
                Scanner scanIn = new Scanner(System.in);
                input = scanIn.nextLine().toLowerCase().trim();
                switch(input)
                {
                    case "add":
                        System.out.println("add Philosopher (p) or Chair (c)?");
                        input = scanIn.nextLine().toLowerCase().trim();
                        if(input.equals("p"))
                        {
                            System.out.println("Hungry? (y) or (n)");
                            input = scanIn.nextLine().toLowerCase().trim();
                            if(input.equals("y"))
                            {
                                userInterface.addPhilosopher(true);
                            }
                            else if(input.equals("n"))
                            {
                                userInterface.addPhilosopher(false);
                            }
                        }
                        else if ( input.equals("c"))
                        {
                            userInterface.addPlate();
                        }
                        break;
                    case "remove":
                        System.out.println("remove Philosopher (p) or Chair (c)?");
                        input = scanIn.nextLine().toLowerCase().trim();
                        if(input.equals("p"))
                        {
                            System.out.println("Hungry? (y) or (n)");
                            input = scanIn.nextLine().toLowerCase().trim();
                            if(input.equals("y"))
                            {
                                userInterface.removePhilosopher(true);
                            }
                            else if(input.equals("n"))
                            {
                                userInterface.removePhilosopher(false);
                            }
                        }
                        else if ( input.equals("c"))
                        {
                            userInterface.removePlate();
                        }
                        break;
                    default:
                        printUsage();
                }
            }
        }
        catch (Exception e)
        {

        }
    }

    private static void printUsage()
    {
        System.out.println("----- USAGE -----");
        System.out.println("Write 'add' or 'remove' to choose what to do.");
        System.out.println("Then follow the instructions");
        System.out.println("----- USAGE -----");
    }
}
