import EngineManager.EngineApi;
import EngineManager.Manager;

import java.util.List;
import java.util.Scanner;

public class UIapi {


    Scanner inputStream;

    EngineApi engine;


    public UIapi(Manager engine) {
        this.inputStream = new Scanner(System.in);
        this.engine = engine;
    }


    public void runSystem()
    {
        boolean exit = false;
        boolean correctInput;
        System.out.println("Welcome to the Stepper application!");
        while(!exit)
        {
            correctInput = false;
            printMenu();
            while(!correctInput)
            {
                Integer userChoice = getIntInput() + 1;
                if (userChoice != null) {
                    if (userChoice >= 0 && userChoice <= 6) {
                        processInput(userChoice, exit);
                        correctInput = true;
                    }
                    else
                        System.out.println("Incorrect index, please enter an index between 1-6");
                }
            }

        }
        System.out.println("Goodbye!");
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
        System.out.printf("Please enter the index of the desired action [number] :");
    }

    public void processInput(int index, boolean exit)
    {
        switch (index) {
            case 1:
                //Load XML
                break;
            case 2:
                showFlowsDefinitions();
                break;
            case 3:
                //Execute flow;
                break;
            case 4:
                showFlowsHistory();
                break;
            case 5:
                //Show statistics;
            case 6:
                exit = true;
        }

    }






    public void showFlowsDefinitions()
    {
        System.out.println("Please choose one of the following flows to get its full definition:");
        int userChoice = chooseFlow();

        if(userChoice != -1)
        {
            System.out.println(engine.getFlowDefinition(userChoice));
        }
    }

    public void showFlowsHistory()
    {
        Integer userChoice = null;
        System.out.println("Please choose one of the following flows to get its past execution data:");
        List<String> initialList = engine.getInitialHistoryList();
        if(initialList.size() > 0) {
            printIndexedList(initialList, "\n");
            userChoice = getUserIndexInput(initialList.size());

            if (userChoice != -1) {
                System.out.println(engine.getFullHistoryData(userChoice));
            }
        }
        else System.out.println("There are currently no past executions");
    }




    public int chooseFlow()
    {
        Integer userChoice = null;
        List<String> flowNames = engine.getFlowsNames();
        if(flowNames.size() > 0) {
            printIndexedList(flowNames);
            userChoice = getUserIndexInput(flowNames.size());
        }
        else {
            System.out.println("There are currently no defined flows in the system.\n You can load flows to the system by using command 1 in the main menu.");
            userChoice = - 2;
        }

        return userChoice;
    }

    public boolean checkIndexInRange(int lastIndex, int index)
    {
        if(index > lastIndex || index < 0)
        {
            System.out.println("Incorrect index, please enter an index between 1-" + lastIndex +", or 0 to go back to the menu");
            return false;
        }
        else
            return true;
    }

    public int getUserIndexInput(int lastIndex)
    {
        boolean correctInput = false;
        Integer userChoice = null;
        while (!correctInput) {
            System.out.println("Enter the index of the desired flow, or 0 to go back to the menu [number] : ");
            userChoice = getIntInput();
            if(userChoice != null)
            {
                correctInput = checkIndexInRange(lastIndex,userChoice);
            }
        }
        return userChoice - 1;
    }


    public void printIndexedList(List<String> list)
    {
        printIndexedList(list, "");
    }

    public void printIndexedList(List<String> list, String changeAfterIndex)
    {
        int index = 1;
        for(String data : list)
        {
            System.out.println(index +". " + changeAfterIndex + data);
            index++;
        }
        System.out.println("0. Exit");
    }








    public Integer getIntInput()
    {
        Integer res = null;

        try {
            res = inputStream.nextInt();

        } catch (Exception e) {
            System.out.println("Incorrect input. please enter an Integer only");
            inputStream.nextLine();
        }

        return res;
    }





}
