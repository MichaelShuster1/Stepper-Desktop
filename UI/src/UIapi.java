import DTO.InputData;
import DTO.InputsDTO;
import DTO.ResultDTO;
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
            printMainMenu();
            while(!correctInput)
            {
                Integer userChoice = getIntInput();
                if (userChoice != null)
                {
                    if (userChoice >= 1 && userChoice <= 8)
                    {
                        exit = processInput(userChoice);
                        correctInput = true;
                    }
                    else
                        System.out.println("Incorrect index, please enter an index between 1-8");
                }
            }

        }
        System.out.println("Goodbye!");
    }

/*
    public Boolean InitialMenu()
    {
        Integer userChoice;
        Boolean exitFromApp = false,exitFromMenu=false;
        System.out.println("Welcome to the Stepper application!");

        while (!exitFromMenu)
        {
            printInitialMenu();

            do {
                userChoice = getIntInput();
            } while (userChoice == null);

            switch (userChoice)
            {
                case 1:
                    exitFromMenu=true;
                    break;
                case 2:
                    exitFromMenu=loadSystemDataFromFile();
                    break;
                case 3:
                    exitFromMenu=true;
                    exitFromApp = true;
                    break;
                default:
                    System.out.println("Incorrect index, please enter an index between 1-3");
                    break;
            }
        }
        return exitFromApp;
    }

 */

   /* public void printInitialMenu()
    {
        System.out.println("Please select one of the following commands:");
        System.out.println("1. Start with a fresh system");
        System.out.println("2. Load system from a file");
        System.out.println("3. Exit");
        System.out.println("Please enter the index of the desired action [number] :");
    }

    */

    public void printMainMenu()
    {
        System.out.println("The main menu:");
        System.out.println("Please select one of the following commands:");
        System.out.println("1. Load new XML file");
        System.out.println("2. Show current flows definitions");
        System.out.println("3. Execute a flow");
        System.out.println("4. Show past flows executions");
        System.out.println("5. Show statistics");
        System.out.println("6. Load previous system state from a file");
        System.out.println("7. Save the current system's parameters to a file");
        System.out.println("8. Exit");
        System.out.println("Please enter the index of the desired action [number] :");
    }

    public Boolean processInput(int index)
    {
        Boolean exit = false;
        switch (index)
        {
            case 1:
                loadXMLFile();
                break;
            case 2:
                showFlowsDefinitions();
                break;
            case 3:
                ExecuteFlow();
                break;
            case 4:
                showFlowsHistory();
                break;
            case 5:
                showStatistics();
                break;
            case 6:
                loadSystemDataFromFile();
                break;
            case 7:
                saveSystemDataToFile();
                break;
            case 8:
                exit = true;
                break;
        }
        return exit;
    }

    public void loadXMLFile() {
        String path;
        System.out.println("Please enter the full path of the XML file: ");
        path = inputStream.nextLine();
        engine.loadXmlFile(path);
    }


    public void saveSystemDataToFile()
    {
        String pathFile;
        System.out.println("Please enter the full path of the file to save the system to (including the file name):");
        pathFile=inputStream.nextLine();
        ResultDTO result= engine.saveDataOfSystemToFile(pathFile);
        System.out.println(result.getMessage());
    }

    public boolean loadSystemDataFromFile()
    {
        String pathFile;
        System.out.println("Please enter the full path of the file to load the system from:");
        pathFile=inputStream.nextLine();
        ResultDTO result=engine.loadDataOfSystemFromFile(pathFile);
        System.out.println(result.getMessage());
        return result.getStatus();
    }


    public void showStatistics()
    {
        System.out.println(engine.getStatistics());
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
        else System.out.println("There are currently no past executions\n");
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


    private void printIndexedList(List<String> list)
    {
        printIndexedList(list, "");
    }

    private void printIndexedList(List<String> list, String changeAfterIndex)
    {
        int index = 1;
        for (String data : list) {
            System.out.println(index + ". " + changeAfterIndex + data);
            index++;
        }
        System.out.println("0. Exit");
    }


    private Integer getIntInput()
    {
        Integer res = null;

        try
        {
            res = inputStream.nextInt();
            if(inputStream.hasNextLine())
                inputStream.nextLine();
        }
        catch (Exception e)
        {
            System.out.println("Incorrect input. please enter an Integer only");
            inputStream.nextLine();
        }

        return res;
    }


    public void ExecuteFlow()
    {
        InputsDTO inputsInfo;
        Integer flowIndex,choice,size;
        String inputMenu,data;
        boolean flowReady=false,runFlow= false;

        flowIndex=chooseFlow();

        if(flowIndex==CODE.BACK.getNumVal()||flowIndex==CODE.EMPTY.getNumVal())
            return;

        inputsInfo=engine.getFlowInputs(flowIndex);
        inputMenu=createInputMenu(inputsInfo);
        size=inputsInfo.getNumberOfInputs();


        while (!runFlow)
        {
            System.out.println(inputMenu);

            flowReady=engine.IsFlowReady();

            if(flowReady)
                System.out.println((size+1)+".Execute the flow");

            System.out.println("Enter the number 0 to go back to the main menu");
            System.out.println("Enter a number between 1 to " + size + " to enter the data  of the desired input you want");
            if(flowReady)
                System.out.println("Enter the number "+ (size+1) + " to to execute the current flow");
            if(!flowReady)
                System.out.println("When all mandatory inputs will be inserted, you will be able to execute the flow");
            System.out.println("Please enter your choice: ");

            do
            {
                choice = getIntInput();
            } while (choice==null);



            if(choice<=size&& choice>=1)
            {
                String inputName,user_string="";
                InputData input=inputsInfo.getFreeInput(choice-1);

                inputName=input.getSystemName();
                user_string=input.getUserString();

                System.out.println(user_string+":");

                data = inputStream.nextLine();
                if(inputName.equals("LINE"))
                {
                    Integer integer=Integer.parseInt(data)-1;
                    data=integer.toString();
                }

                ResultDTO res=engine.processInput(inputName, data);
                System.out.println(res.getMessage()+"\n");
            }
            else if(choice.equals(size+1) && flowReady)
            {
                runFlow=true;
                System.out.println(engine.runFlow());
            }
            else if(choice.equals(0))
                return;
            else {
                if(flowReady)
                   System.out.println("Incorrect index, please enter an index between 1-" + size+1 + ", or 0 to go back to the menu");
                else
                    System.out.println("Incorrect index, please enter an index between 1-" + size + ", or 0 to go back to the menu");
            }

        }
    }



    private String createInputMenu(InputsDTO inputsInfo)
    {
        String inputMenu="",inputToShow="";
        Integer size=inputsInfo.getNumberOfInputs();
        inputMenu+="The input menu:\n";
        inputMenu+="0. Exit\n";
        for (int i=0;i<size;i++)
        {
            InputData input=inputsInfo.getFreeInput(i);
            inputToShow=input.getSystemName().toLowerCase().replace("_"," ");

            inputToShow+=" [";
            if(input.getNecessity())
                inputToShow+="mandatory";
            else
                inputToShow+="optional";
            inputToShow+="]";

            inputMenu+=(i+1)+". "+inputToShow+"\n";
        }
        return  inputMenu;
    }


}
