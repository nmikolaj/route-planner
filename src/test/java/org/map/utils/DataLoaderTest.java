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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataLoaderTest {

    @TempDir
    Path tempDir;
    private Path testFilePath;

    @BeforeEach
    void setUp() throws IOException {
        testFilePath = tempDir.resolve("test.json");
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
                      "size": 1.00
                    },
                    {
                      "name": "Old Oak Junction",
                      "xPosition": 150,
                      "yPosition": 420,
                      "size": 1.00
                    },
                    {
                      "name": "Crestview Point",
                      "xPosition": 200,
                      "yPosition": 410,
                      "size": 1.00
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
                    }
                  ]
                }""";
        Files.writeString(testFilePath, testJson);
    }

    @Test
    void testLoadPoints() {
        List<Point> points = DataLoader.loadPoints(testFilePath.toString());
        assertEquals(4, points.size());
        assertTrue(points.stream().anyMatch(point -> point.getName().equals("Hilltop Lookout")));
        assertTrue(points.stream().anyMatch(point -> point.getName().equals("Ridgeway Pass")));
    }

    @Test
    void testLoadRoutes() {
        List<Point> points = DataLoader.loadPoints(testFilePath.toString());
        List<Route> routes = DataLoader.loadRoutes(testFilePath.toString(), points);

        assertEquals(1, routes.size());
        Route highlandTrail = routes.get(0);
        assertEquals("Highland Trail", highlandTrail.getName());
        assertEquals(4, highlandTrail.getPoints().size());
        assertTrue(highlandTrail.getPoints().stream().anyMatch(point -> point.getName().equals("Hilltop Lookout")));
        assertTrue(highlandTrail.getPoints().stream().anyMatch(point -> point.getName().equals("Crestview Point")));
    }
}
