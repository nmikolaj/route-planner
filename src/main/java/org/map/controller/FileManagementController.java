package org.map.controller;

import org.map.model.Route;
import org.map.model.Point;
import org.map.utils.DataWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FileManagementController {

    private final MapController mapController;

    public FileManagementController(MapController mapController) {
        this.mapController = mapController;
    }

    public void saveData(String mapName) throws IOException {
        List<Point> points = mapController.getPoints();
        List<Route> routes = mapController.getRoutes();
        String backgroundImage = mapController.getBackgroundImage();

        String filePath = "src/main/resources/saved_maps/" + mapName + ".json";
        DataWriter.savePointsAndRoutes(filePath, points, routes, backgroundImage);
    }

    public List<String> getSavedMaps() {
        File folder = new File("src/main/resources/saved_maps");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        List<String> mapNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                mapNames.add(file.getName().replace(".json", ""));
            }
        }
        return mapNames;
    }

    public boolean deleteMap(String mapName) {
        File file = new File("src/main/resources/saved_maps/" + mapName + ".json");
        return file.exists() && file.delete();
    }
}