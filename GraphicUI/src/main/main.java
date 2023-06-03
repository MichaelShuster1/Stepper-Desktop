package main;

import controllers.AppController;
import enginemanager.Manager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;

public class main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource =getClass().getResource("/resources/fxml/MainScreen.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(resource);
        Parent root = loader.load(resource.openStream());
        AppController controller = loader.getController();
        controller.setModel(new Manager());
        controller.setPrimaryStage(primaryStage);


        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double widthFraction = 0.8;
        double heightFraction = 0.8;
        double desiredWidth = screenBounds.getWidth() * widthFraction;
        double desiredHeight = screenBounds.getHeight() * heightFraction;

        Scene scene = new Scene(root,desiredWidth,desiredHeight);
        Image icon = new Image(getClass().getResource("/resources/pictures/Icon.png").toExternalForm());
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Stepper");
        primaryStage.setScene(scene);
        primaryStage.show();

    }


}
