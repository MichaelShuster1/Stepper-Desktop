package controllers.flowdefinition;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DefinitionController {
    @FXML
    private Button button123;



    @FXML
    void changeButtonName(ActionEvent event) {
        System.out.println("click123");
    }
}
