import EngineManager.EngineApi;
import EngineManager.Manager;
import Flow.Flow;

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


    public UIapi() {
        this.inputStream = new Scanner(System.in);
        this.engine = new Manager();
    }


    public void setEngine(EngineApi engine)
    {
        this.engine = engine;
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
        System.out.println("4. Show past flows executions");
        System.out.println("5. Show statistics");
        System.out.println("6. Exit");
    }


    public void processInput()
    {



    }
    public int chooseFlow()
    {
        Integer userChoice=0;
        boolean correctInput = false;
        List<String> flowNames = engine.getFlowsNames();
        int index = 1;

        System.out.println("0. Exit");
        for(String name : flowNames)
        {
            System.out.println(index +". " + name);
            index++;
        }

        while (!correctInput)
        {
            System.out.println("Enter the index of the desired flow, or 0 to go back to the menu [number] : ");
            userChoice = getIntInput();
            if(userChoice != null)
            {
                if(userChoice > flowNames.size() || userChoice < 0)
                {
                    System.out.println("Incorrect index, please enter an index between 1-" + flowNames.size() +", or 0 to go back to the menu");
                }
                else
                {
                    correctInput = true;
                }
            }
        }
        return userChoice-1;
    }


    public Integer getIntInput()
    {
        Integer res = null;

        try
        {
            res = inputStream.nextInt();
        }
        catch (Exception e)
        {
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
                System.out.println("wrong number entered");

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
