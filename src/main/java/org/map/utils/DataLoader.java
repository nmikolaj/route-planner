package org.map.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.map.model.Route;
import org.map.model.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    public static List<Point> loadPoints(String filePath) {
        List<Point> points = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray pointsJson = jsonObject.getJSONArray("points");

            for (int i = 0; i < pointsJson.length(); i++) {
                JSONObject pointJson = pointsJson.getJSONObject(i);
                Point point = new Point(
                        pointJson.getString("name"),
                        pointJson.getDouble("xPosition"),
                        pointJson.getDouble("yPosition"),
                        pointJson.getDouble("size")
                );
                points.add(point);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }

    public static List<Route> loadRoutes(String filePath, List<Point> points) {
        List<Route> routes = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray routesJson = jsonObject.getJSONArray("routes");

            for (int i = 0; i < routesJson.length(); i++) {
                JSONObject routeJson = routesJson.getJSONObject(i);
                List<Point> routePoints = new ArrayList<>();
                JSONArray pointNames = routeJson.getJSONArray("points");

                for (int j = 0; j < pointNames.length(); j++) {
                    String pointName = pointNames.getString(j);
                    points.stream()
                            .filter(point -> point.getName().equals(pointName))
                            .findFirst()
                            .ifPresent(routePoints::add);
                }
                Route route = new Route(
                        routeJson.getString("name"),
                        routePoints
                );
                routes.add(route);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return routes;
    }

    public static String loadBackgroundImage(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            return jsonObject.optString("backgroundImage", "");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
