package org.map.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.map.model.Route;
import org.map.model.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataWriter {

    public static void savePointsAndRoutes(String filePath, List<Point> points,
                                                  List<Route> routes, String backgroundImage) throws IOException {
        JSONObject jsonObject = new JSONObject();
        JSONArray pointsJson = new JSONArray();

        for (Point point : points) {
            JSONObject pointJson = new JSONObject();
            pointJson.put("name", point.getName());
            pointJson.put("xPosition", point.getXPosition());
            pointJson.put("yPosition", point.getYPosition());
            pointJson.put("size", point.getSize());
            pointsJson.put(pointJson);
        }

        jsonObject.put("points", pointsJson);

        JSONArray routesJson = new JSONArray();
        for (Route route : routes) {
            JSONObject routeJson = new JSONObject();
            JSONArray pointNames = new JSONArray();

            for (Point point : route.getPoints()) {
                pointNames.put(point.getName());
            }

            routeJson.put("name", route.getName());
            routeJson.put("points", pointNames);
            routesJson.put(routeJson);
        }

        jsonObject.put("routes", routesJson);
        jsonObject.put("backgroundImage", backgroundImage);

        writeToFile(filePath, jsonObject.toString(4));
    }


    private static void writeToFile(String filePath, String content) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
        }
    }
}