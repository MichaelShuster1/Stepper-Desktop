# Stepper-Desktop

Developed as part of Aviad Cohen's Java course at the Academic College of Tel-Aviv Yaffo. 

The Stepper is a workflow\pipeline system that enables assembling different	scenarios (called flows) from common components (called steps), including executing them and	producing required results.

# Overview
A step is a piece of independent logical unit that is able to carry out a specific action (e.g. deleting files, exporting textual data, sending http request).

Each step has its own inputs that are required for the step to execute its action, and outputs that are produced after the execution.

Given a set of steps, we can make a "connection" between the different steps so that the output of one will serve as the input of the other. The collection of steps connected together in this way is called a flow.

The Stepper system allows to take the different step definitions and combine them into flows to produce different work flows (models)
we can then proceed to exectue the flow, activating the execution of each step within the flow.

# Features
* The flows definitions are loaded into the system by an XML file 

* **Automatic mapping** - the system will automatically map the outputs to the various inputs of the steps in the flow

* **Custom mapping** -  define custom input/output mapping via the XML file

* **Name aliasing** - change the name of a step/input/output to distinguish them in case there is more than one in a flow.

* **Initial values** - define an initial value for a step's input

* **Continuation** - use the information and values ​​produced in a previous flow execution in another flow's execution. This ability allows an immediate and fast connection between one flow execution and another.

* **Flow execution** - the user can choose a flow, provide all the required inputs, and execute the flow

* **Asynchronous execution** - the ability to execute multiple flows concurrently.

* **Flow execution data** - view all the data and information produced as the flow executes (updated as the flow progresses)

* **Flow execution history** - view all past execution including all the executions's data.

* **Execution rerun** - by selecting a past execution, it is possible to rerun the same execution (same inputs) immediately

# Top section of screen
* Load XML file by clicking on the button to the left.
* Enable/disable animation by the radio button to the right.
* Select the system style from 3 different option.

# Flows dashboard screen

* This screen allows you to view all the available flows currently loaded into the system.
* View the flows full definition information by selecting a specific flow from the table.
* Select the desired flow for execution and click on "Execute flow" to proceed to the execution screen


# Flows execution screen
* This screen allows you to execute the selected flow.
* At the top are the mandatory and optional inputs of the flow, the flow will be ready to execute when all the mandatory inputs will be filled.
* Left click on an input to insert data into it.
* Right click on an input to view or delete its current data.
* When the flow is ready, click on execute to run the flow.
* The execution progress will update at the lower part of the screen.
* It is possible to click on each completed step in the table to view its specific execution data, as well as the full flow execution progress.
* Once the flow finishes its execution, it is possible to rerun the flow or apply continuation (if available) by the respective buttons that become available.

# Executions history screen
* This screen allows you to view all past completed executions
* Select a flow in the table to view its full data execution (it is possible to select each step of a flow).
* It is possible to rerun the selected flow by clicking on the rerun button.
* If the flow have a defined continuation it is possible to apply the continuation by clicking on "Continuation options"

# Statistics screen
* This page allows you to view the statistics of past executions.
* View how many times and how much time on average it took for each step/flow to execute
* View the statistics data in graph view by clicking on the buttons at the bottom. 


## Authors

This project was made by [Igal Kaminski](https://www.github.com/igalKa) & [Michael Shuster](https://github.com/MichaelShuster1)
