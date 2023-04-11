import EngineManager.EngineApi;
import EngineManager.Manager;

import java.util.List;
import java.util.Scanner;

public class UIapi
{

    public enum CODE
    {
        EMPTY(-2), BACK(-1);

        private int numVal;

        CODE(int numVal)
        {
            this.numVal = numVal;
        }

        public int getNumVal()
        {
            return numVal;
        }
    }

    Scanner inputStream;

    EngineApi engine;


    public UIapi(Manager engine) {
        this.inputStream = new Scanner(System.in);
        this.engine = engine;
    }


    public void setEngine(EngineApi engine)
    {
        this.engine = engine;
    }

    public void runSystem()
    {
        Boolean exit = false;
        boolean correctInput;
        System.out.println("Welcome to the Stepper application!");
        while(!exit)
        {
            correctInput = false;
            printMenu();
            while(!correctInput)
            {
                Integer userChoice = getIntInput();
                if (userChoice != null)
                {
                    if (userChoice >= 1 && userChoice <= 6)
                    {
                        exit = processInput(userChoice);
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
        System.out.println("4. Show past flows executions");
        System.out.println("5. Show statistics");
        System.out.println("6. Exit");
        System.out.printf("Please enter the index of the desired action [number] :");
    }

    public Boolean processInput(int index)
    {
        Boolean exit = false;
        switch (index)
        {
            case 1:
                //Load XML
                break;
            case 2:
                showFlowsDefinitions();
                break;
            case 3:
                getInputsAndExecuteFlow();
                break;
            case 4:
                showFlowsHistory();
                break;
            case 5:
                //Show statistics;
                break;
            case 6:
                exit = true;
                break;
        }
        return exit;

    }


    public void showFlowsDefinitions()
    {
        System.out.println("Please choose one of the following flows to get its full definition:");
        int userChoice = chooseFlow();

        if(userChoice != CODE.BACK.getNumVal())
        {
            System.out.println(engine.getFlowDefinition(userChoice));
        }
    }

    public void showFlowsHistory()
    {
        Integer userChoice = null;
        List<String> initialList = engine.getInitialHistoryList();
        if(initialList.size() > 0) {
            System.out.println("Please choose one of the following flows to get its past execution data:");
            printIndexedList(initialList, "\n");
            userChoice = getUserIndexInput(initialList.size());

            if (userChoice != CODE.BACK.getNumVal()) {
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
            System.out.println("There are currently no defined flows in the system.\n You can load flows to the system by using command 1 in the main menu.\n");
            userChoice = CODE.EMPTY.getNumVal();
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
        for (String data : list) {
            System.out.println(index + ". " + changeAfterIndex + data);
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


    public void getInputsAndExecuteFlow()
    {
        List<String> inputsInfo;
        Integer flowIndex,choice,size;
        String inputMenu,data;
        boolean flowReady=false,runFlow= false;

        flowIndex=chooseFlow();

        if(flowIndex==CODE.BACK.getNumVal()||flowIndex==CODE.EMPTY.getNumVal())
            return;

        inputsInfo=engine.getFlowInputs(flowIndex);
        inputMenu=createInputMenu(inputsInfo);
        size=inputsInfo.size();

        while (!runFlow)
        {
            System.out.println("0. Exit");
            System.out.println(inputMenu);

            if(flowReady)
                System.out.println((size+1)+".Execute the flow");

            System.out.println("Enter the number 0 to go back to the menu");
            System.out.println("Enter a number between 1 to " + size + " to enter the data  of the desired input you want");
            System.out.println("Enter the number "+ (size+1) + " to to execute the current flow");
            System.out.println("Please enter your choice: ");

            do
            {
                choice = getIntInput();
            } while (choice==null);


            if(choice<=size&& choice>=1)
            {
                String inputName,user_string="";
                String [] inputInfo = inputsInfo.get(choice-1).split(" ");

                inputName = inputInfo[0];

                user_string=inputInfo[2];
                for(int i=3;i<inputInfo.length;i++)
                    user_string+=" "+inputInfo[i];

                System.out.println(user_string);

                inputStream.nextLine();
                data = inputStream.nextLine();

                flowReady=engine.processInput(inputName, data);
            }
            else if(choice.equals(size+1))
            {
                runFlow=true;
                System.out.println(engine.runFlow());
            }
            else
                System.out.println("Wrong number entered");

        }
    }


    private String createInputMenu(List<String> inputsInfo)
    {
        String inputMenu="",inputToShow;
        int index=1;
        for (String inputInfo:inputsInfo)
        {
            String [] strings =inputInfo.split(" ");
            inputToShow=strings[0]+ " ["+strings[1]+"]";
            inputToShow=inputToShow.toLowerCase().replace("_"," ");
            inputMenu+=index+"."+inputToShow+"\n";
            index++;
        }
        return  inputMenu;
    }


}
