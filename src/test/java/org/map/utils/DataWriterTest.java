package org.map.utils;

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

import static org.junit.jupiter.api.Assertions.assertTrue;

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
                new Point("Ridgeway Pass", 100, 450, 1.00)
        );

        List<Point> highlandTrailPoints = new ArrayList<>();
        highlandTrailPoints.add(testPoints.get(0));
        highlandTrailPoints.add(testPoints.get(1));

        testRoutes = List.of(
                new Route("Highland Trail", highlandTrailPoints)
        );

        testBackgroundImage = "background.jpg";
    }

    @Test
    void testSavePointsAndRoutes() throws IOException {
        DataWriter.savePointsAndRoutes(testFilePath.toString(), testPoints, testRoutes, testBackgroundImage);

        String fileContent = Files.readString(testFilePath);

        assertTrue(fileContent.contains("Hilltop Lookout"));
        assertTrue(fileContent.contains("Ridgeway Pass"));

        assertTrue(fileContent.contains("Highland Trail"));
        assertTrue(fileContent.contains("background.jpg")); // Check presence
    }
}
