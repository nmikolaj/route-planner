package org.map.view;

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
import javafx.scene.image.Image;
import javafx.util.Duration;

import org.map.controller.MapController;
import org.map.model.Route;
import org.map.model.Point;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapView extends Canvas {
    private final MapController controller;
    private PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
    private Image backgroundImage;
    private Point currentHoveredPoint = null;
    private Point currentDraggedPoint = null;
    private boolean showCoordinates = false;
    private Map<String, Color> routeColors = new HashMap<>();

    public MapView(MapController controller) {
        this.controller = controller;
        this.setWidth(1024);
        this.setHeight(768);
        drawMap();

        initializeRouteColors();
        addMouseMotionListener();
        addRightClickListener();
        addMouseDragListener();
    }

    public void initializeRouteColors() {
        List<Route> routes = controller.getRoutes();
        for (Route route : routes) {
            int hash = route.getName().hashCode();
            Random rand = new Random(hash); // Names hash as a seed for random colors
            Color color = new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1);
            routeColors.put(route.getName(), color);
        }
    }

    private Color getRouteColor(Point point) {
        for (Route route : controller.getRoutes()) {
            if (route.contains(point)) {
                Color baseColor = routeColors.getOrDefault(route.getName(), Color.WHITE);
                return baseColor.deriveColor(0, 1, 1.6, 1);
            }
        }
        return Color.WHITE;
    }

    public void drawMap() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, getWidth(), getHeight());
        }
        drawPoints();
        drawRoutes();
        if (showCoordinates) {
            drawCoordinates();
        }
    }

    public void setBackgroundImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            backgroundImage = new Image("file:" + imagePath);
        } else {
            backgroundImage = null;
        }
        drawMap(); // Redraw with new background
    }

    private void drawPoints() {
        GraphicsContext gc = getGraphicsContext2D();
        List<Point> points = controller.getPoints();

        for (Point point : points) {
            double pointSize = point.getSize()*10;
            Color pointColor = getRouteColor(point);
            drawPoint(gc, point.getXPosition(), point.getYPosition(), pointSize, pointColor);
        }
    }

    private void drawPoint(GraphicsContext gc, double x, double y, double size, Color color) {
        gc.setFill(color);

        double[] xPoints = { x, x - size / 2, x + size / 2 };
        double[] yPoints = { y, y - size, y - size };
        gc.fillPolygon(xPoints, yPoints, 3);

        gc.fillOval(x - size / 2, y - size / 2 - size, size, size);
    }

    private void drawRoutes() {
        GraphicsContext gc = getGraphicsContext2D();
        List<Route> routes = controller.getRoutes();

        for (Route route : routes) {
            Color lineColor = routeColors.getOrDefault(route.getName(), Color.BLUE);
            gc.setFont(new Font("Arial", 16));
            gc.setStroke(lineColor);
            gc.setLineWidth(2);
            gc.setFill(lineColor);

            List<Point> pointsInRoute = route.getPoints();
            for (int i = 0; i < pointsInRoute.size() - 1; i++) {
                Point start = pointsInRoute.get(i);
                Point end = pointsInRoute.get(i + 1);
                gc.strokeLine(start.getXPosition(), start.getYPosition(), end.getXPosition(), end.getYPosition());
            }

            // Draw name of route next to first point
            if (!pointsInRoute.isEmpty()) {
                Point firstPoint = pointsInRoute.get(0);

                gc.setStroke(Color.WHITE);
                gc.strokeText(route.getName(), firstPoint.getXPosition(), firstPoint.getYPosition() - 15);
                gc.fillText(route.getName(), firstPoint.getXPosition(), firstPoint.getYPosition() - 15);
            }
        }
    }

    private void addMouseMotionListener() {
        this.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            Point foundPoint = null;

            List<Point> points = controller.getPoints();
            for (Point point : points) {
                if (Math.abs(mouseX - point.getXPosition()) < 10 && Math.abs(mouseY - point.getYPosition()) < 10) {
                    foundPoint = point;
                    break;
                }
            }

            if (foundPoint != null && foundPoint != currentHoveredPoint) {
                currentHoveredPoint = foundPoint;
                drawPointName(foundPoint);
            } else if (foundPoint == null && currentHoveredPoint != null) {
                hidePointName();
                currentHoveredPoint = null;
            }
        });
    }

    private void drawPointName(Point point) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.setFill(Color.BLACK);
        gc.strokeText(point.getName(), point.getXPosition() + 10, point.getYPosition() - 10);
        gc.fillText(point.getName(), point.getXPosition() + 10, point.getYPosition() - 10);
    }

    private void hidePointName() {
        clearCanvas();
        drawMap();
    }

    private void addRightClickListener() {
        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                double mouseX = event.getX();
                double mouseY = event.getY();

                Point clickedPoint = null;
                List<Point> points = controller.getPoints();
                for (Point point : points) {
                    if (Math.abs(mouseX - point.getXPosition()) < 10 && Math.abs(mouseY - point.getYPosition()) < 10) {
                        clickedPoint = point;
                        break;
                    }
                }
                if (clickedPoint != null) {
                    showContextMenu(event.getScreenX(), event.getScreenY(), clickedPoint);
                }
            }
        });
    }

    private void showContextMenu(double x, double y, Point point) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editNameItem = new MenuItem("Change Name");
        editNameItem.setOnAction(e -> editPointName(point));

        MenuItem editSizeItem = new MenuItem("Change Size");
        editSizeItem.setOnAction(e -> editPointSize(point));

        MenuItem addToRouteItem = new MenuItem("Add to Route");
        addToRouteItem.setOnAction(e -> addToRoute(point));

        MenuItem removePointItem = new MenuItem("Delete Point");
        removePointItem.setOnAction(e -> controller.removePoint(point, this));

        contextMenu.getItems().addAll(editNameItem, editSizeItem, addToRouteItem, removePointItem);

        contextMenu.show(this, x, y);
    }

    private void editPointName(Point point) {
        TextInputDialog dialog = new TextInputDialog(point.getName());
        dialog.setTitle("Change Name");
        dialog.setHeaderText("Enter new name for " + point.getName());
        dialog.showAndWait().ifPresent(newName -> {
            controller.setPointName(point, newName);
            drawMap();
        });
    }

    private void editPointSize(Point point) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Edit Point Size");
        dialog.setHeaderText("Adjust size for " + point.getName());

        // Create the slider for size
        Slider sizeSlider = new Slider(1, 10, point.getSize());
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);

        VBox dialogContent = new VBox(sizeSlider);
        dialogContent.setSpacing(10);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(dialogContent);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return sizeSlider.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newSize -> {
            controller.setPointSize(point, newSize);
            drawMap();
        });
    }

    private void addToRoute(Point point) {
        List<String> routeNames = controller.getRouteNames();

        ChoiceDialog<String> dialog = new ChoiceDialog<>("", routeNames);
        dialog.setTitle("Add to Route");
        dialog.setHeaderText("Select a Route for " + point.getName());

        dialog.showAndWait().ifPresent(selectedRouteName -> {
            controller.addPointToRoute(point, selectedRouteName, this);
            drawMap();
        });
    }

    private void addMouseDragListener() {
        this.setOnMousePressed(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            List<Point> points = controller.getPoints();
            for (Point point : points) {
                if (Math.abs(mouseX - point.getXPosition()) < 10 && Math.abs(mouseY - point.getYPosition()) < 10) {
                    currentDraggedPoint = point;
                    break;
                }
            }
        });

        this.setOnMouseDragged(event -> {
            if (currentDraggedPoint != null) {
                currentDraggedPoint.setXPosition(event.getX());
                currentDraggedPoint.setYPosition(event.getY());
                drawMap();
            }
        });

        this.setOnMouseReleased(event -> {
            if (currentDraggedPoint != null) {
                drawMap();
                currentDraggedPoint = null; // Set dragged point back to null
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
