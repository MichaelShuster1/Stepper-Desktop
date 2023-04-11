import EngineManager.EngineApi;
import EngineManager.Manager;

import java.util.Scanner;

public class UIapi {

    Scanner inputStream;

    EngineApi engine;


    public UIapi() {
        this.inputStream = new Scanner(System.in);
        this.engine = new Manager();
    }


    public void runSystem()
    {
        boolean exit = false;
        System.out.println("Welcome to the Stepper application!");
        while(!exit)
        {
            printMenu();
            processInput();
        }
    }

    public void printMenu()
    {
        System.out.println("Please select one of the following commands:");
        System.out.println("1. Load new XML file");
        System.out.println("2. Show current flows definitions");
        System.out.println("3. Execute a flow");
        System.out.println("4. Show past flows execution");
        System.out.println("5. Show Stepper statistics");
        System.out.println("6. Exit");
    }

    public void processInput()
    {

    }




}
