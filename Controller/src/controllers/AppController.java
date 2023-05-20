package controllers;

import controllers.flowdefinition.DefinitionController;
import controllers.statistics.StatisticsController;
import dto.InputsDTO;
import enginemanager.EngineApi;
import enginemanager.Manager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import controllers.flowexecution.ExecutionController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

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
    private StackPane statisticsComponent;
    @FXML
    private StatisticsController statisticsComponentController;

    @FXML
    private Button loadXML;

    @FXML
    private Label loadedXML;

    @FXML
    private TabPane tabPaneView;

    private EngineApi engine;

    private Stage primaryStage;




    @FXML
    public void initialize() {
        executionComponentController.setAppController(this);
        definitionComponentController.setAppController(this);
        statisticsComponentController.setAppController(this);
    }


    public void setModel(Manager engine) {
        this.engine = engine;
        executionComponentController.setEngine(engine);
        definitionComponentController.setEngine(engine);
        statisticsComponentController.setEngine(engine);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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
            loadedXML.setText("Currently loaded file: " + selectedFile.getAbsolutePath());
            definitionComponentController.addTable();
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
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        return  selectedFile;
    }


    public void streamFlowFrom1To2(int index) {
        executionComponentController.setTabView(getFlowInputs(index));
        tabPaneView.getSelectionModel().select(2);
    }

    public void updateStatistics() {
        statisticsComponentController.fillTablesData();
    }

}



