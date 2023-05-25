package main;

import dto.FlowDefinitionDTO;
import dto.FreeInputDefinitionDTO;
import dto.OutputDefintionDTO;
import dto.StepDefinitionDTO;
import enginemanager.Manager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.List;
import java.util.Set;


public class test extends Application {


    private StackPane flowDefinition;
    private StackPane header;
    private VBox availableFlows;
    private VBox flowDetails;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Manager manager = new Manager();
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        Text headerText = new Text("Stepper");
        headerText.setFont(Font.font("Roboto",FontWeight.BOLD,24));


        // Create the button and text field
        Button loadXMLButton = new Button("Load XML File");
        loadXMLButton.setBackground(new Background(new BackgroundFill(Color.LAWNGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        loadXMLButton.setAlignment(Pos.BASELINE_LEFT);
        Label loadedXML = new Label("No xml file loaded yet");
        loadedXML.setFont(Font.font(16));
        loadedXML.setStyle("-fx-border-color: black; -fx-background-color: lightpink");
        loadedXML.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(loadedXML,Priority.ALWAYS);



        HBox headerBox = new HBox(headerText);
        headerBox.setAlignment(Pos.CENTER);


        HBox inputBox = new HBox(10, loadXMLButton, loadedXML);
        inputBox.setAlignment(Pos.BASELINE_LEFT);
        HBox.setMargin(loadXMLButton,new Insets(0,0,0,10));
        HBox.setMargin(loadedXML,new Insets(0,10,0,0));


        // Create the buttons
        Button button1 = new Button("Flows Definition");
        Button button2 = new Button("Flow Execution");
        Button button3 = new Button("Executions History");
        Button button4 = new Button("Statistics");

        // Create the panes
        availableFlows = createPaneWithColor(Color.NAVY);
        flowDetails = createPaneWithColor(Color.GREEN.BLUEVIOLET);

        // Set the preferred size for the panes
        availableFlows.setPrefSize(200, 300);
        flowDetails.setPrefSize(600, 300);

        // Create an HBox for the panes
        HBox paneBox = new HBox(10, availableFlows, flowDetails);
        paneBox.setPadding(new Insets(10));

        ColumnConstraints panelWidth = new ColumnConstraints();
        panelWidth.setPercentWidth(40);
        panelWidth.setFillWidth(true);

        ColumnConstraints contentWidth = new ColumnConstraints();
        contentWidth.setPercentWidth(60);
        contentWidth.setFillWidth(true);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setFillHeight(true);
        rowConstraints.setVgrow(Priority.ALWAYS);

        GridPane pane = new GridPane();
        pane.addRow(0, availableFlows,flowDetails);
        pane.getColumnConstraints().setAll(panelWidth, contentWidth);
        pane.getRowConstraints().setAll(rowConstraints);
        pane.setHgap(20);

        // Create a StackPane as the main container
        flowDefinition = new StackPane();
        flowDefinition.setPadding(new Insets(10,10,10,10));
        flowDefinition.setBackground(new Background(new BackgroundFill(Color.BEIGE, CornerRadii.EMPTY, Insets.EMPTY)));
        StackPane.setMargin(pane,new Insets(10,10,10,10));
        flowDefinition.setStyle("-fx-border-color: black");
        flowDefinition.getChildren().add(pane);

        loadedXML.prefWidthProperty().bind(flowDefinition.prefWidthProperty());
        HBox mainCenterScreen = new HBox(flowDefinition);
        HBox.setHgrow(flowDefinition,Priority.ALWAYS);



        // Set the StackPane as the center component of the root BorderPane
        root.setCenter(mainCenterScreen);
        BorderPane.setMargin(mainCenterScreen,new Insets(10,10,10,10));

        // Create an HBox for the buttons
        HBox buttonBox = new HBox(10, button1, button2,button3,button4);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setMargin(button4,new Insets(0,20,0,0));
        // Add the buttonBox to the top of the root BorderPane
        //root.setTop(buttonBox);


        VBox header = new VBox(10,headerBox,inputBox,buttonBox);
        root.setTop(header);

        // Set action events for the buttons
        button1.setOnAction(e -> showPane(flowDefinition));
        button2.setOnAction(e -> showPane(flowDetails));

        availableFlows.prefWidthProperty().bind(flowDefinition.widthProperty().divide(20/9));
        availableFlows.prefHeightProperty().bind(flowDefinition.heightProperty());
        flowDetails.prefWidthProperty().bind(flowDefinition.widthProperty().divide(20/11));
        flowDetails.prefHeightProperty().bind(flowDefinition.heightProperty());

        loadXMLButton.setOnAction(e -> openFileChooser(primaryStage,manager,availableFlows,loadedXML,flowDetails));

        // Show the scene
        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setTitle("Stepper");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createPaneWithColor(Color color) {
        VBox pane = new VBox();
        pane.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setPadding(new Insets(10,10,10,10));
        return pane;
    }

    private void showPane(Pane pane) {
        // Bring the selected pane to the front
        pane.toFront();
    }

    private void openFileChooser(Stage stage,Manager manager,Pane pane, Label loadedXML, Pane definition) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");

        // Set initial directory (optional)
        fileChooser.setInitialDirectory(new File("C:\\Users\\Igal\\Desktop\\New folder (2)"));

        // Add file extension filters (optional)
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                manager.loadXmlFile(selectedFile.getAbsolutePath());
                List<String> names = manager.getFlowsNames();
                pane.getChildren().clear();
                VBox flows = new VBox();
                flows.setAlignment(Pos.CENTER);
                for(int i=0;i<names.size();i++)
                {
                    Button currButton = new Button(names.get(i));
                    currButton.setAlignment(Pos.CENTER);
                    currButton.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(currButton,Priority.ALWAYS);

                    int finalI = i;
                    currButton.setOnAction(e -> showFlowDefinition(finalI,manager,definition));
                    flows.getChildren().add(currButton);
                }

                definition.getChildren().clear();
                loadedXML.setText("Currently loaded file: " + selectedFile.getAbsolutePath());
                pane.getChildren().add(flows);

            }
            catch (Exception ex)
            {
                Duration TEXT_CHANGE_DURATION = Duration.seconds(6);
                Timeline timeline = new Timeline();
                String INITIAL_TEXT = loadedXML.getText();

                // Define the initial keyframe
                KeyFrame initialKeyFrame = new KeyFrame(Duration.ZERO, e -> {
                    loadedXML.setText(ex.getMessage());
                    loadedXML.setTextFill(Color.RED);
                });

                // Define the final keyframe
                KeyFrame finalKeyFrame = new KeyFrame(TEXT_CHANGE_DURATION, e -> {
                    loadedXML.setText(INITIAL_TEXT);
                    loadedXML.setTextFill(Color.BLACK);
                });

                // Add keyframes to the timeline
                timeline.getKeyFrames().addAll(initialKeyFrame, finalKeyFrame);

                // Play the timeline
                timeline.play();
            }
        }
    }

    private void showFlowDefinition(int index,Manager manager,Pane definition)
    {
        FlowDefinitionDTO flowDefinition = manager.getFlowDefinition(index);
        String data;
        data = "SELECTED FLOW DATA:\n\n";
        data += "Flow name: " + flowDefinition.getName() + "\n";
        data += "Flow description: " + flowDefinition.getDescription() + "\n\n";
        data += getStrFormalOutputs(flowDefinition.getFormal_outputs()) + "\n";
        data += getStrReadOnlyStatus(flowDefinition.isReadOnly());
        data += "------------------------------\n";
        data += getStrStepsData(flowDefinition.getSteps());
        data += "------------------------------\n";
        data += getStrFreeInputs(flowDefinition.getFreeInputs());
        data += "------------------------------\n";
        data += getStrOutPuts(flowDefinition.getOutputs());
        data += "------------------------------\n";

        Text flowDefinitionText = new Text(data);
        //flowDefinitionText.setWrapText(true);
        //flowDefinitionText.setPrefWidth(definition.getPrefWidth());
        flowDefinitionText.setFont(Font.font(16));
        TextFlow textFlow = new TextFlow(flowDefinitionText);
        textFlow.prefWidthProperty().bind(definition.widthProperty());
        Accordion accordion = new Accordion();
        Label test = new Label("data for the spend some time step");
        TitledPane step1 = new TitledPane("Spend some time",test);
        accordion.getPanes().add(step1);
        ScrollPane scrollPane = new ScrollPane(textFlow);
        scrollPane.setPrefWidth(definition.getPrefWidth());
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        definition.getChildren().clear();
        definition.getChildren().add(scrollPane);
        definition.getChildren().add(accordion);

    }

    private String getStrFormalOutputs(Set<String> formal_outputs) {
        String res;
        if (formal_outputs.size() > 0) {
            res = "The formal outputs of the flow are:\n";
            for (String currOutput : formal_outputs) {
                res = res + currOutput + "\n";
            }
        } else
            res = "The flow doesn't have formal outputs\n";

        return res;
    }

    private String getStrReadOnlyStatus(boolean read_only) {
        if (read_only)
            return "The flow is Read-Only: YES\n";
        else
            return "The flow is Read-Only: NO\n";
    }

    private String getStrStepsData(List<StepDefinitionDTO> steps) {
        String res = "THE FLOW'S STEPS:\n";
        String currStep;
        for (StepDefinitionDTO step : steps) {
            if (step.getName().equals(step.getDefaultName()))
                currStep = "Step name: " + step.getName() + "\n";
            else {
                currStep = "Step name: " + step.getDefaultName() + "\n";
                currStep += "Step alias: " + step.getName() + "\n";
            }
            if (step.isReadOnly())
                currStep = currStep + "The step is Read-Only: YES\n";
            else
                currStep = currStep + "This step is Read-Only: No\n";
            currStep = currStep + "\n";
            res += currStep;
        }

        return res;
    }

    private String getStrFreeInputs(List<FreeInputDefinitionDTO> flowFreeInputs) {
        String res;
        if (flowFreeInputs.isEmpty())
            res = "The flow have no free inputs\n";
        else {
            res = "Flow's free input's are:\n\n";
            String currInput;
            for (FreeInputDefinitionDTO freeInput : flowFreeInputs) {
                currInput = "Name: " + freeInput.getName() + "\n";
                currInput += "Type: " + freeInput.getType() + "\n";
                currInput += "Steps that are related to that input: ";
                for (String stepName : freeInput.getRelatedSteps())
                    currInput += stepName + ", ";
                currInput = currInput.substring(0, currInput.length() - 2);
                currInput += "\n";
                if (freeInput.isMandatory())
                    currInput += "This input is mandatory: Yes\n\n";
                else
                    currInput += "This input is mandatory: No\n\n";
                res += currInput;
            }
        }

        return res;
    }

    public String getStrOutPuts(List<OutputDefintionDTO> outputs) {
        String res = "THE FLOW'S OUTPUTS:\n";

        for (OutputDefintionDTO output : outputs) {
            res += "Output name: " + output.getName() + "\n";
            res += "Type: " + output.getType() + "\n";
            res += "Belongs to step: " + output.getStepName() + "\n\n";
        }

        if (outputs.size() > 0)
            return res;
        else
            return "THIS FLOW HAVE NO OUTPUTS";
    }

}
