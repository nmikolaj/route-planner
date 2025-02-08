package org.map;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.map.controller.MapController;
import org.map.controller.FileManagementController;
import org.map.view.MapView;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MapController controller = new MapController("src/main/resources/home_map.json");
        MapView view = new MapView(controller);
        FileManagementController fileManagementController = new FileManagementController(controller);

        HBox controlPanel = new HBox(15);
        controlPanel.setLayoutY(768);
        controlPanel.setPrefWidth(1024); // Matches scene width
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setStyle("-fx-background-color: #052743;");
        controlPanel.setPadding(new Insets(20,40,20,40));

        String plusIconPath = getResourcePath("/images/plus.png");
        if (plusIconPath != null) {
            Image plusIcon = new Image(plusIconPath);
            ImageView plusIconView = new ImageView(plusIcon);
            plusIconView.setFitWidth(50);
            plusIconView.setFitHeight(50);

            Button addPointButton = new Button();
            addPointButton.setGraphic(plusIconView);
            addPointButton.getStyleClass().add("icon-button");
            addPointButton.setPrefSize(110, 60);
            addPointButton.setOnAction(event -> controller.addPoint(view));
            controlPanel.getChildren().add(addPointButton);
        }

        String showGridIconPath = getResourcePath("/images/grid.png");
        if (showGridIconPath != null) {
            Image showCoordinatesIcon = new Image(showGridIconPath);
            ImageView showCoordinatesIconView = new ImageView(showCoordinatesIcon);
            showCoordinatesIconView.setFitWidth(50);
            showCoordinatesIconView.setFitHeight(50);

            Button showCoordinatesButton = new Button();
            showCoordinatesButton.setGraphic(showCoordinatesIconView);
            showCoordinatesButton.getStyleClass().add("icon-button");
            showCoordinatesButton.setPrefSize(110, 60);
            showCoordinatesButton.setOnAction(event -> view.showCoordinates());
            controlPanel.getChildren().add(showCoordinatesButton);
        }

        String changeBackgroundIconPath = getResourcePath("/images/background.png");
        if (changeBackgroundIconPath != null) {
            Image changeBackgroundIcon = new Image(changeBackgroundIconPath);
            ImageView changeBackgroundIconView = new ImageView(changeBackgroundIcon);
            changeBackgroundIconView.setFitWidth(50);
            changeBackgroundIconView.setFitHeight(50);

            Button changeBackgroundButton = new Button();
            changeBackgroundButton.setGraphic(changeBackgroundIconView);
            changeBackgroundButton.getStyleClass().add("icon-button");
            changeBackgroundButton.setPrefSize(110, 60);
            changeBackgroundButton.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select Background Image");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
                File selectedFile = fileChooser.showOpenDialog(primaryStage);

                if (selectedFile != null) {
                    String imagePath = selectedFile.getAbsolutePath();
                    controller.setBackgroundImage(imagePath);
                    view.setBackgroundImage(imagePath);
                }
            });
            controlPanel.getChildren().add(changeBackgroundButton);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Takes up all the available space
        controlPanel.getChildren().add(spacer);
        Button manageRoutesButton = new Button("Routes");
        manageRoutesButton.setPrefSize(140, 50);
        manageRoutesButton.setStyle("-fx-font-size: 16px;");

        ContextMenu routeMenu = new ContextMenu();

        MenuItem addRouteItem = new MenuItem("Add Route");
        addRouteItem.setOnAction(event -> controller.addRoute(view));

        MenuItem deleteRouteItem = new MenuItem("Delete Route");
        deleteRouteItem.setOnAction(event -> {
            List<String> routeNames = controller.getRouteNames();
            ChoiceDialog<String> dialog = new ChoiceDialog<>("", routeNames);
            dialog.setTitle("Delete Route");
            dialog.setHeaderText("Select a route to delete:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> controller.removeRoute(name, view));
        });
        routeMenu.getItems().addAll(addRouteItem, deleteRouteItem);

        // Open context menu above the button
        manageRoutesButton.setOnAction(event -> {
            double x = manageRoutesButton.localToScreen(0, 0).getX();
            double y = manageRoutesButton.localToScreen(0, 0).getY() - routeMenu.getHeight();
            routeMenu.show(manageRoutesButton, x, y);
        });
        controlPanel.getChildren().add(manageRoutesButton);

        Button manageMapsButton = new Button("Maps");
        manageMapsButton.setPrefSize(140, 50);
        manageMapsButton.setStyle("-fx-font-size: 16px;");

        ContextMenu mapsMenu = new ContextMenu();

        MenuItem saveMapItem = new MenuItem("Save Map");
        saveMapItem.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("MyMap");
            dialog.setTitle("Save Map");
            dialog.setHeaderText("Enter a name for your map:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(mapName -> {
                try {
                    fileManagementController.saveData(mapName);
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Map saved successfully.");
                    successAlert.setTitle("Save Successful");
                    successAlert.setHeaderText(null);
                    successAlert.showAndWait();
                } catch (IOException e) {
                    Alert failureAlert = new Alert(Alert.AlertType.ERROR, "Error saving map: " + e.getMessage());
                    failureAlert.setTitle("Save Failed");
                    failureAlert.setHeaderText(null);
                    failureAlert.showAndWait();
                }
            });
        });

        MenuItem deleteMapItem = new MenuItem("Delete Map");
        deleteMapItem.setOnAction(event -> {
            List<String> savedMaps = fileManagementController.getSavedMaps();

            if (savedMaps.isEmpty()) {
                Alert noMapsAlert = new Alert(Alert.AlertType.INFORMATION, "No saved maps available for deletion.");
                noMapsAlert.setTitle("Delete Map");
                noMapsAlert.setHeaderText(null);
                noMapsAlert.showAndWait();
                return;
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>("", savedMaps);
            dialog.setTitle("Delete Map");
            dialog.setHeaderText("Select a map to delete:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(mapName -> {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to delete the map: " + mapName + "?", ButtonType.YES, ButtonType.NO);
                confirmAlert.setTitle("Confirm Deletion");
                confirmAlert.setHeaderText(null);

                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        boolean success = fileManagementController.deleteMap(mapName);
                        Alert alert;
                        if (success) {
                            alert = new Alert(Alert.AlertType.INFORMATION, "Map deleted successfully.");
                        } else {
                            alert = new Alert(Alert.AlertType.ERROR, "Failed to delete the map.");
                        }
                        alert.setTitle(success ? "Deletion Successful" : "Deletion Failed");
                        alert.setHeaderText(null);
                        alert.showAndWait();
                    }
                });
            });
        });
        mapsMenu.getItems().addAll(saveMapItem, deleteMapItem);

        manageMapsButton.setOnAction(event -> {
            double x = manageMapsButton.localToScreen(0, 0).getX();
            double y = manageMapsButton.localToScreen(0, 0).getY() - mapsMenu.getHeight();
            mapsMenu.show(manageMapsButton, x, y);
        });
        controlPanel.getChildren().add(manageMapsButton);

        ChoiceBox<String> loadMapChoiceBox = new ChoiceBox<>();
        loadMapChoiceBox.setPrefSize(160, 50);
        loadMapChoiceBox.setStyle("-fx-font-size: 16px;");
        loadMapChoiceBox.setValue("Load Map");

        loadMapChoiceBox.setOnShowing(event -> {
            loadMapChoiceBox.getItems().clear();
            List<String> savedMaps = fileManagementController.getSavedMaps();

            if (savedMaps.isEmpty()) {
                loadMapChoiceBox.getItems().add("No Maps Available");
            } else {
                loadMapChoiceBox.getItems().addAll(savedMaps);
            }
        });
        loadMapChoiceBox.setOnAction(event -> {
            String selectedMap = loadMapChoiceBox.getValue();
            if (selectedMap != null && !selectedMap.equals("No Maps Available")) {
                String filePath = "src/main/resources/saved_maps/" + selectedMap + ".json";
                controller.loadMap(filePath, view);
            }
        });
        controlPanel.getChildren().add(loadMapChoiceBox);

        Group root = new Group();
        root.getChildren().addAll(view, controlPanel);

        Scene scene = new Scene(root, 1024, 865);
        scene.setFill(Color.BLACK);
        String cssPath = getResourcePath("/styles.css");
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        }
        primaryStage.setTitle("RoutePlanner");
        primaryStage.setScene(scene);
        primaryStage.show();
        view.drawMap();
    }

    private String getResourcePath(String resource) {
        var url = getClass().getResource(resource);
        if (url == null) {
            System.err.println("Error: Resource not found -> " + resource);
            return null;
        }
        return url.toExternalForm();
    }
}
