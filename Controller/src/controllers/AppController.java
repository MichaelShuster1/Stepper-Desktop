package controllers;

import controllers.flowdefinition.DefinitionController;
import controllers.flowexecution.ExecutionController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

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
    private Button loadXML;

    @FXML
    private Label loadedXML;





    @FXML
    private void loadXMLFile(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        // Set initial directory (optional)
        fileChooser.setInitialDirectory(new File("C:\\Users\\Igal\\Desktop\\New folder (2)"));
        //File selectedFile = fileChooser.showOpenDialog(stage);
        System.out.println("click");
        loadedXML.setText("hello");

    }

}

