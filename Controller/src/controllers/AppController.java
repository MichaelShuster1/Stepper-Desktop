package controllers;

import controllers.flowdefinition.DefinitionController;
import enginemanager.Manager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import controllers.flowexecution.ExecutionController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

public class AppController {


    Manager engine;

    Stage primaryStage;
    @FXML
    private StackPane definitionComponent;

    @FXML
    private DefinitionController definitionComponentController;


    @FXML
    private StackPane executionComponent;

    @FXML
    private ExecutionController executionComponentController;

    @FXML
    private Button loadXML;

    @FXML
    private Label loadedXML;


    public void setModel(Manager engine) {
        this.engine = engine;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void loadXMLFile(ActionEvent event) {
       File selectedFile = openFileChooserAndGetFile();
       if(selectedFile == null)
           return;

        try {
            engine.loadXmlFile(selectedFile.getAbsolutePath());
            loadedXML.setText("Currently loaded file: " + selectedFile.getAbsolutePath());
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


}



