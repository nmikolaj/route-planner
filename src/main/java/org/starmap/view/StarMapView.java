package org.starmap.view;

import javafx.animation.PauseTransition;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.input.MouseButton;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ChoiceDialog;
import javafx.util.Duration;

import org.starmap.controller.StarMapController;
import org.starmap.model.Constellation;
import org.starmap.model.Star;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StarMapView extends Canvas {
    private final StarMapController controller;
    private PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
    private Star currentHoveredStar = null;
    private Star currentDraggedStar = null;
    private boolean showCoordinates = false;
    private Map<String, Color> constellationColors = new HashMap<>();

    public StarMapView(StarMapController controller) {
        this.controller = controller;
        this.setWidth(1024); // Set canvas width
        this.setHeight(768); // Set canvas height
        drawMap();

        initializeConstellationColors();
        addMouseMotionListener();
        addRightClickListener();
        addMouseDragListener();
    }

    private void initializeConstellationColors() {
        List<Constellation> constellations = controller.getConstellations();
        for (Constellation constellation : constellations) {
            int hash = constellation.getName().hashCode();
            Random rand = new Random(hash); // Use hash as a seed for random generator
            Color color = new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1);
            constellationColors.put(constellation.getName(), color);
        }
    }

    public void drawMap() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight()); // Set background to black
        drawStars();
        drawConstellations();
        if (showCoordinates) {
            drawCoordinates();
        }
    }

    private void drawStars() {
        GraphicsContext gc = getGraphicsContext2D();
        List<Star> stars = controller.getStars();
        for (Star star : stars) {
            double brightnessScale = star.getBrightness() / 2.0; // Scale brightness
            double starSize = 2 + (5 - brightnessScale); // Calculate star size
            Color starColor = Color.hsb(60, 0.5, 1 - 0.2 * brightnessScale); // Color based on brightness
            drawStar(gc, star.getXPosition(), star.getYPosition(), starSize, starColor);
        }
    }

    private void drawStar(GraphicsContext gc, double x, double y, double size, Color color) {
        double[] xPoints = new double[10];
        double[] yPoints = new double[10];
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 5 * i;
            double radius = i % 2 == 0 ? size : size / 2;
            xPoints[i] = x + radius * Math.sin(angle);
            yPoints[i] = y - radius * Math.cos(angle);
        }
        gc.setStroke(color);
        gc.strokePolyline(xPoints, yPoints, 10);
    }

    private void drawConstellations() {
        GraphicsContext gc = getGraphicsContext2D();
        List<Constellation> constellations = controller.getConstellations();

        for (Constellation constellation : constellations) {
            Color lineColor = constellationColors.getOrDefault(constellation.getName(), Color.BLUE);
            gc.setStroke(lineColor);
            gc.setLineWidth(1);
            gc.setFill(lineColor);
            gc.setFont(new Font("Arial", 14));

            List<Star> starsInConstellation = constellation.getStars();
            for (int i = 0; i < starsInConstellation.size() - 1; i++) {
                Star start = starsInConstellation.get(i);
                Star end = starsInConstellation.get(i + 1);
                gc.strokeLine(start.getXPosition(), start.getYPosition(), end.getXPosition(), end.getYPosition());
            }

            // Draw name of constellation next to first star
            if (!starsInConstellation.isEmpty()) {
                Star firstStar = starsInConstellation.get(0);
                gc.fillText(constellation.getName(), firstStar.getXPosition(), firstStar.getYPosition() - 15);
            }
        }
    }

    private void addMouseMotionListener() {
        this.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            Star foundStar = null;

            List<Star> stars = controller.getStars();
            for (Star star : stars) {
                if (Math.abs(mouseX - star.getXPosition()) < 10 && Math.abs(mouseY - star.getYPosition()) < 10) {
                    foundStar = star;
                    break;
                }
            }

            if (foundStar != null && foundStar != currentHoveredStar) {
                currentHoveredStar = foundStar;
                drawStarName(foundStar);
            } else if (foundStar == null && currentHoveredStar != null) {
                hideStarName();
                currentHoveredStar = null;
            }
        });
    }

    private void drawStarName(Star star) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillText(star.getName(), star.getXPosition() + 10, star.getYPosition() - 10);
    }

    private void hideStarName() {
        clearCanvas();
        drawMap();
    }

    private void addRightClickListener() {
        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                double mouseX = event.getX();
                double mouseY = event.getY();

                Star clickedStar = null;
                List<Star> stars = controller.getStars();
                for (Star star : stars) {
                    if (Math.abs(mouseX - star.getXPosition()) < 10 && Math.abs(mouseY - star.getYPosition()) < 10) {
                        clickedStar = star;
                        break;
                    }
                }
                if (clickedStar != null) {
                    showContextMenu(event.getScreenX(), event.getScreenY(), clickedStar);
                }
            }
        });
    }

    private void showContextMenu(double x, double y, Star star) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editNameItem = new MenuItem("Change Name");
        editNameItem.setOnAction(e -> editStarName(star));

        MenuItem editBrightnessItem = new MenuItem("Change Brightness");
        editBrightnessItem.setOnAction(e -> editStarBrightness(star));

        MenuItem addToConstellationItem = new MenuItem("Add to Constellation");
        addToConstellationItem.setOnAction(e -> addToConstellation(star));

        MenuItem removeStarItem = new MenuItem("Delete Star");
        removeStarItem.setOnAction(e -> controller.removeStar(star, this));

        contextMenu.getItems().addAll(editNameItem, editBrightnessItem, addToConstellationItem, removeStarItem);

        contextMenu.show(this, x, y);
    }

    private void editStarName(Star star) {
        TextInputDialog dialog = new TextInputDialog(star.getName());
        dialog.setTitle("Change Name");
        dialog.setHeaderText("Enter new name for " + star.getName());
        dialog.showAndWait().ifPresent(newName -> {
            controller.setStarName(star, newName);
            drawMap();
        });
    }

    private void editStarBrightness(Star star) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Edit Star Brightness");
        dialog.setHeaderText("Adjust brightness for " + star.getName());

        // Create the slider for brightness
        Slider brightnessSlider = new Slider(0, 10, star.getBrightness());
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setShowTickMarks(true);

        VBox dialogContent = new VBox(brightnessSlider);
        dialogContent.setSpacing(10);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(dialogContent);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return brightnessSlider.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newBrightness -> {
            controller.setStarBrightness(star, newBrightness);
            drawMap();
        });
    }

    private void addToConstellation(Star star) {
        List<String> constellationNames = controller.getConstellationNames();

        ChoiceDialog<String> dialog = new ChoiceDialog<>("", constellationNames);
        dialog.setTitle("Add to Constellation");
        dialog.setHeaderText("Select a Constellation for " + star.getName());

        dialog.showAndWait().ifPresent(selectedConstellationName -> {
            controller.addStarToConstellation(star, selectedConstellationName, this);
            drawMap();
        });
    }

    private void addMouseDragListener() {
        this.setOnMousePressed(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            List<Star> stars = controller.getStars();
            for (Star star : stars) {
                if (Math.abs(mouseX - star.getXPosition()) < 10 && Math.abs(mouseY - star.getYPosition()) < 10) {
                    currentDraggedStar = star;
                    break;
                }
            }
        });

        this.setOnMouseDragged(event -> {
            if (currentDraggedStar != null) {
                currentDraggedStar.setXPosition(event.getX());
                currentDraggedStar.setYPosition(event.getY());
                drawMap();
            }
        });

        this.setOnMouseReleased(event -> {
            if (currentDraggedStar != null) {
                drawMap();
                currentDraggedStar = null; // Set dragged star back to null
            }
        });
    }

    public void showCoordinates() {
        showCoordinates = !showCoordinates;
        drawMap();
    }

    private void drawCoordinates() {
        GraphicsContext gc = this.getGraphicsContext2D();

        double width = getWidth();
        double height = getHeight();

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(0.5);

        int step = 22; // Distance

        for (int x = 0; x <= width; x += step) {
            gc.strokeLine(x, 0, x, height);
        }

        for (int y = 0; y <= height; y += step) {
            gc.strokeLine(0, y, width, y);
        }
    }

    private void clearCanvas() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
    }
}
