package org.map.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.map.model.Route;
import org.map.model.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataWriterTest {

    @TempDir
    Path tempDir;
    private Path testFilePath;

    private List<Point> testPoints;
    private List<Route> testRoutes;
    private String testBackgroundImage;

    @BeforeEach
    void setUp() {
        testFilePath = tempDir.resolve("test.json");

        testPoints = List.of(
                new Point("Hilltop Lookout", 50, 400, 1.00),
                new Point("Ridgeway Pass", 100, 450, 2.50)
        );

        List<Point> highlandTrailPoints = new ArrayList<>(testPoints);

        testRoutes = List.of(
                new Route("Highland Trail", highlandTrailPoints)
        );

        testBackgroundImage = "background.jpg";
    }

    @Test
    void testSavePointsAndRoutes() throws IOException {
        DataWriter.savePointsAndRoutes(testFilePath.toString(), testPoints, testRoutes, testBackgroundImage);

        // Read file content and parse JSON
        String fileContent = Files.readString(testFilePath);
        JSONObject jsonObject = new JSONObject(fileContent);

        // Validation
        assertTrue(jsonObject.has("points"));
        assertTrue(jsonObject.has("routes"));
        assertTrue(jsonObject.has("backgroundImage"));

        assertEquals("background.jpg", jsonObject.getString("backgroundImage"));

        JSONArray pointsJson = jsonObject.getJSONArray("points");
        assertEquals(2, pointsJson.length());

        JSONObject point1 = pointsJson.getJSONObject(0);
        assertEquals("Hilltop Lookout", point1.getString("name"));
        assertEquals(50, point1.getDouble("xPosition"));
        assertEquals(400, point1.getDouble("yPosition"));
        assertEquals(1.00, point1.getDouble("size"));

        JSONObject point2 = pointsJson.getJSONObject(1);
        assertEquals("Ridgeway Pass", point2.getString("name"));
        assertEquals(100, point2.getDouble("xPosition"));
        assertEquals(450, point2.getDouble("yPosition"));
        assertEquals(2.50, point2.getDouble("size"));

        JSONArray routesJson = jsonObject.getJSONArray("routes");
        assertEquals(1, routesJson.length());

        JSONObject route = routesJson.getJSONObject(0);
        assertEquals("Highland Trail", route.getString("name"));

        JSONArray routePoints = route.getJSONArray("points");
        assertEquals(2, routePoints.length());
        assertEquals("Hilltop Lookout", routePoints.getString(0));
        assertEquals("Ridgeway Pass", routePoints.getString(1));
    }

    @Test
    void testSaveEmptyData() throws IOException {
        List<Point> emptyPoints = new ArrayList<>();
        List<Route> emptyRoutes = new ArrayList<>();

        DataWriter.savePointsAndRoutes(testFilePath.toString(), emptyPoints, emptyRoutes, "");

        String fileContent = Files.readString(testFilePath);
        JSONObject jsonObject = new JSONObject(fileContent);

        assertTrue(jsonObject.has("points"));
        assertTrue(jsonObject.has("routes"));
        assertTrue(jsonObject.has("backgroundImage"));

        assertEquals("", jsonObject.getString("backgroundImage"));
        assertEquals(0, jsonObject.getJSONArray("points").length());
        assertEquals(0, jsonObject.getJSONArray("routes").length());
    }

    @Test
    void testFileIsCreated() throws IOException {
        DataWriter.savePointsAndRoutes(testFilePath.toString(), testPoints, testRoutes, testBackgroundImage);
        assertTrue(Files.exists(testFilePath));
    }

    @Test
    void testInvalidFilePathThrowsException() {
        assertThrows(IOException.class, () -> {
            DataWriter.savePointsAndRoutes("/invalid-path/test.json", testPoints, testRoutes, testBackgroundImage);
        });
    }
}
