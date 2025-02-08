package org.map.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.map.model.Route;
import org.map.model.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataLoaderTest {

    @TempDir
    Path tempDir;
    private Path testFilePath;
    private Path emptyFilePath;
    private Path malformedFilePath;

    @BeforeEach
    void setUp() throws IOException {
        testFilePath = tempDir.resolve("test.json");
        emptyFilePath = tempDir.resolve("empty.json");
        malformedFilePath = tempDir.resolve("malformed.json");

        String testJson = """
                {
                  "points": [
                    {
                      "name": "Hilltop Lookout",
                      "xPosition": 50,
                      "yPosition": 400,
                      "size": 1.00
                    },
                    {
                      "name": "Ridgeway Pass",
                      "xPosition": 100,
                      "yPosition": 450,
                      "size": 1.50
                    },
                    {
                      "name": "Old Oak Junction",
                      "xPosition": 150,
                      "yPosition": 420,
                      "size": 2.00
                    },
                    {
                      "name": "Crestview Point",
                      "xPosition": 200,
                      "yPosition": 410,
                      "size": 2.50
                    }
                  ],
                  "routes": [
                    {
                      "name": "Highland Trail",
                      "points": [
                        "Hilltop Lookout",
                        "Ridgeway Pass",
                        "Old Oak Junction",
                        "Crestview Point"
                      ]
                    },
                    {
                      "name": "Forest Walk",
                      "points": [
                        "Nonexistent Point",
                        "Hilltop Lookout"
                      ]
                    }
                  ],
                  "backgroundImage": "background.jpg"
                }""";

        String malformedJson = """
                {
                  "points": [
                    {
                      "name": "Hilltop Lookout"
                      "xPosition": 50
                      "yPosition": 400
                      "size": 1.00
                    }
                  ]
                """;

        Files.writeString(testFilePath, testJson);
        Files.writeString(emptyFilePath, "{}");
        Files.writeString(malformedFilePath, malformedJson);
    }

    @Test
    void testLoadPoints() {
        List<Point> points = DataLoader.loadPoints(testFilePath.toString());

        assertEquals(4, points.size());

        Point firstPoint = points.get(0);
        assertEquals("Hilltop Lookout", firstPoint.getName());
        assertEquals(50, firstPoint.getXPosition());
        assertEquals(400, firstPoint.getYPosition());
        assertEquals(1.00, firstPoint.getSize());

        Point lastPoint = points.get(3);
        assertEquals("Crestview Point", lastPoint.getName());
        assertEquals(200, lastPoint.getXPosition());
        assertEquals(410, lastPoint.getYPosition());
        assertEquals(2.50, lastPoint.getSize());
    }

    @Test
    void testLoadRoutes() {
        List<Point> points = DataLoader.loadPoints(testFilePath.toString());
        List<Route> routes = DataLoader.loadRoutes(testFilePath.toString(), points);

        assertEquals(2, routes.size());

        Route highlandTrail = routes.get(0);
        assertEquals("Highland Trail", highlandTrail.getName());
        assertEquals(4, highlandTrail.getPoints().size());

        Route forestWalk = routes.get(1);
        assertEquals("Forest Walk", forestWalk.getName());
        assertEquals(1, forestWalk.getPoints().size()); // Nonexistent point should not be loaded
        assertEquals("Hilltop Lookout", forestWalk.getPoints().get(0).getName());
    }

    @Test
    void testLoadBackgroundImage() {
        String backgroundImage = DataLoader.loadBackgroundImage(testFilePath.toString());
        assertEquals("background.jpg", backgroundImage);
    }

    @Test
    void testLoadPointsFromEmptyFile() {
        List<Point> points = DataLoader.loadPoints(emptyFilePath.toString());
        assertNotNull(points);
        assertEquals(0, points.size());
    }

    @Test
    void testLoadRoutesFromEmptyFile() {
        List<Point> points = DataLoader.loadPoints(emptyFilePath.toString());
        List<Route> routes = DataLoader.loadRoutes(emptyFilePath.toString(), points);
        assertNotNull(routes);
        assertEquals(0, routes.size());
    }

    @Test
    void testLoadBackgroundImageFromEmptyFile() {
        String backgroundImage = DataLoader.loadBackgroundImage(emptyFilePath.toString());
        assertEquals("", backgroundImage);
    }

    @Test
    void testLoadMalformedFile() {
        List<Point> points = DataLoader.loadPoints(malformedFilePath.toString());
        assertNotNull(points);
        assertEquals(0, points.size());
    }
}
