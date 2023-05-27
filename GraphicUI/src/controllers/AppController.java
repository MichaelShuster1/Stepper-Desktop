package controllers;

import controllers.flowdefinition.DefinitionController;
import controllers.statistics.StatisticsController;
import controllers.history.HistoryController;
import dto.FlowExecutionDTO;
import dto.InputsDTO;
import enginemanager.EngineApi;
import enginemanager.Manager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import controllers.flowexecution.ExecutionController;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import progress.ProgressTracker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppController {

    @FXML
    private StackPane definitionComponent;

    @FXML
    private DefinitionController definitionComponentController;

    @FXML
    private StackPane executionComponent;

    @FXML
    private ExecutionController executionComponentController;

    @FXML
    private StackPane historyComponent;

    @FXML
    private HistoryController historyComponentController;

    @FXML
    private StackPane statisticsComponent;
    @FXML
    private StatisticsController statisticsComponentController;

    @FXML
    private Button loadXML;

    @FXML
    private Label loadedXML;

    @FXML
    private TabPane tabPaneView;
    @FXML
    private ChoiceBox<String> styleChoiceView;

    private EngineApi engine;

    private ProgressTracker progressTracker;

    private Stage primaryStage;




    @FXML
    public void initialize() {
        executionComponentController.setAppController(this);
        definitionComponentController.setAppController(this);
        historyComponentController.setAppController(this);
        statisticsComponentController.setAppController(this);
        styleChoiceView.getItems().addAll("DEFAULT","DARK","MIDNIGHT");
        styleChoiceView.setValue("DEFAULT");
        styleChoiceView.setOnAction(e->setStyle());
        setTab(3);
    }

    private void setStyle() {
        String choice= styleChoiceView.getValue();
        primaryStage.getScene().getStylesheets().clear();
        switch (choice)
        {
            case "DARK":
                primaryStage.getScene().getStylesheets().add(
                        getClass().getResource("../main/Dark.css").toExternalForm());
                break;
            case "MIDNIGHT":
                primaryStage.getScene().getStylesheets().add(
                        getClass().getResource("../main/Midnight.css").toExternalForm());
            case "DEFAULT":
                break;
        }
    }


    public void setModel(Manager engine) {
        this.engine = engine;
        executionComponentController.setEngine(engine);
        definitionComponentController.setEngine(engine);
        statisticsComponentController.setEngine(engine);
        historyComponentController.setEngine(engine);
        progressTracker=new ProgressTracker(this,engine);
        Thread thread=new Thread(progressTracker);
        thread.setDaemon(true);
        thread.start();
    }

    public void addFlowId(String id)
    {
        progressTracker.addFlowId(id);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        executionComponentController.setStage(primaryStage);
        historyComponentController.setStage(primaryStage);
    }


    public InputsDTO getFlowInputs(int index)
    {
        return engine.getFlowInputs(index);
    }


    @FXML
    private void loadXMLFile(ActionEvent event) {
       File selectedFile = openFileChooserAndGetFile();
       if(selectedFile == null)
           return;

        try {
            engine.loadXmlFile(selectedFile.getAbsolutePath());
            clearTabs();
            loadedXML.setText("Currently loaded file: " + selectedFile.getAbsolutePath());
            definitionComponentController.fillTableData();
            statisticsComponentController.createStatisticsTables();
        }
        catch (Exception ex)
        {
            Duration TEXT_CHANGE_DURATION = Duration.seconds(6);
            Timeline timeline = new Timeline();
            String INITIAL_TEXT = loadedXML.getText();

            // Define the initial keyframe
            KeyFrame initialKeyFrame = new KeyFrame(Duration.ZERO, e -> {
                loadedXML.setText("Error: " + ex.getMessage());
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


    private File openFileChooserAndGetFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");

        // Set initial directory (optional)
        //fileChooser.setInitialDirectory(new File("C:\\Users\\Igal\\Desktop\\New folder (2)"));

        // Add file extension filters (optional)
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        return  selectedFile;
    }


    public void streamFlow(int index) {
        progressTracker.resetCurrentFlowId();
        executionComponentController.setTabView(getFlowInputs(index));
        setTab(2);
    }

    public void updateProgressFlow(FlowExecutionDTO flowExecutionDTO)
    {
        executionComponentController.updateProgressFlow(flowExecutionDTO);
    }



    public void setTab(int index)
    {
        tabPaneView.getSelectionModel().select(index);
    }


    public void updateStatistics() {
        statisticsComponentController.fillTablesData();
    }


    public void addRowInHistoryTable(FlowExecutionDTO flowExecutionDTO)
    {
        historyComponentController.addRow(flowExecutionDTO);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void clearTabs() {
        definitionComponentController.clearTab();
        executionComponentController.clearTab();
        historyComponentController.clearTab();
        statisticsComponentController.clearTab();
    }
}



