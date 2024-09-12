package org.starmap;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.starmap.controller.StarMapController;
import org.starmap.controller.FileManagementController;
import org.starmap.view.StarMapView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.IOException;
import java.util.List;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StarMapController controller = new StarMapController("src/main/resources/stars.json");
        StarMapView view = new StarMapView(controller);
        FileManagementController fileManagementController = new FileManagementController(controller);

        HBox controlPanel = new HBox(15);
        controlPanel.setLayoutX(26);
        controlPanel.setLayoutY(768);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setStyle("-fx-background-color: #052743;");
        controlPanel.setPadding(new Insets(20,170,20,170));

        Button addStarButton = new Button("Add Star");
        addStarButton.setPrefSize(150, 80);
        addStarButton.setOnAction(event -> controller.addStar(view));
        controlPanel.getChildren().add(addStarButton);

        Button showCoordinatesButton = new Button("Show Coordinates");
        showCoordinatesButton.setPrefSize(150, 80);
        showCoordinatesButton.setOnAction(event -> view.showCoordinates());
        controlPanel.getChildren().add(showCoordinatesButton);

        Button saveButton = new Button("Save Star Map");
        saveButton.setPrefSize(150, 80);
        saveButton.setOnAction(event -> {
            try {
                fileManagementController.saveData();
            } catch (IOException e) {
                System.out.printf("Error occurred while saving data");
            }
        });
        controlPanel.getChildren().add(saveButton);

        Button loadButton = new Button("Load Star Map");
        loadButton.setPrefSize(150, 80);
        loadButton.setOnAction(event -> {
            String filePath = "src/main/resources/saved_stars_and_constellations.json";
            controller.loadStarMap(filePath, view);
        });
        controlPanel.getChildren().add(loadButton);

        Group root = new Group();
        root.getChildren().addAll(view, controlPanel);

        Scene scene = new Scene(root, 1024, 885);
        scene.setFill(Color.BLACK);
        primaryStage.setTitle("Star Map");
        primaryStage.setScene(scene);
        primaryStage.show();
        view.drawMap(); // Call this after the scene is shown
    }
}
