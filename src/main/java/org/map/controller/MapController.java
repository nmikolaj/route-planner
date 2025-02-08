package org.map.controller;

import javafx.scene.control.TextInputDialog;
import org.map.model.Route;
import org.map.model.Point;
import org.map.utils.DataLoader;
import org.map.view.MapView;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class MapController {
    private List<Point> points;
    private List<Route> routes;
    private String backgroundImage = "";

    public MapController(String dataFilePath) {
        this.points = DataLoader.loadPoints(dataFilePath);
        this.routes = DataLoader.loadRoutes(dataFilePath, points);
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public List<String> getRouteNames() {
        List<String> names = new ArrayList<>();
        for (Route route : routes) {
            names.add(route.getName());
        }
        return names;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getBackgroundImage() { return backgroundImage; }

    public void setBackgroundImage(String imagePath) { this.backgroundImage = imagePath; }

    public void loadMap(String filePath, MapView view) {
        List<Point> loadedPoints = DataLoader.loadPoints(filePath);
        List<Route> loadedRoutes = DataLoader.loadRoutes(filePath, loadedPoints);
        String loadedBackgroundImage = DataLoader.loadBackgroundImage(filePath);

        this.setPoints(loadedPoints);
        this.setRoutes(loadedRoutes);
        this.setBackgroundImage(loadedBackgroundImage);
        view.initializeRouteColors();
        view.setBackgroundImage(loadedBackgroundImage);
        view.drawMap();
    }

    // Get point by its name
    public Optional<Point> getPointByName(String name) {
        return points.stream().filter(point -> point.getName().equalsIgnoreCase(name)).findFirst();
    }
    public void setPointName(Point point, String newName) {
        point.setName(newName);
    }

    public void setPointSize(Point point, double newSize) {
        point.setSize(newSize);
    }

    // Get route by its name
    public Optional<Route> getRouteByName(String name) {
        return routes.stream().filter(route -> route.getName().equalsIgnoreCase(name)).findFirst();
    }

    // Add new point to the map
    public void addPoint(MapView view) {
        int pointIndex = 1;
        String baseName = "New Point";
        String uniqueName = baseName;

        // Ensure unique name
        while (getPointByName(uniqueName).isPresent()) {
            uniqueName = baseName + " " + pointIndex;
            pointIndex++;
        }

        Point point = new Point(uniqueName, 95, 700, 2);
        points.add(point);
        view.drawMap();
    }

    // Remove point from the map
    public void removePoint(Point point, MapView view) {
        points.remove(point);

        for (Route route : routes) {
            if (route.contains(point)) {
                route.removePoint(point);
            }
        }
        view.drawMap();
    }

    public void addRoute(MapView view) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Route");
        dialog.setHeaderText("Enter the name of the new route:");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (getRouteByName(name).isEmpty()) {
                Route newRoute = new Route(name, new ArrayList<>());
                routes.add(newRoute);

                view.initializeRouteColors(); // Refresh colors in the view
                view.drawMap();
            } else {
                System.out.println("Route already exists!");
            }
        });
    }

    public void removeRoute(String name, MapView view) {
        routes.removeIf(route -> route.getName().equalsIgnoreCase(name));
        view.drawMap();
    }

    public void addPointToRoute(Point point, String newRouteName, MapView view) {
        // Remove point from its current route
        for (Route route : routes) {
            route.removePoint(point);
        }

        routes.stream()
                .filter(c -> c.getName().equals(newRouteName))
                .findFirst()
                .ifPresent(route -> route.addPoint(point));
        view.drawMap();
    }
}
