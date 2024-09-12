package org.starmap.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.starmap.model.Constellation;
import org.starmap.model.Star;

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

    private List<Star> testStars;
    private List<Constellation> testConstellations;

    @BeforeEach
    void setUp() {
        testFilePath = tempDir.resolve("test.json");

        testStars = List.of(
                new Star("Sirius", 100, 200, 1.46),
                new Star("Canopus", 150, 250, 0.72)
        );

        List<Star> taurusStars = new ArrayList<>();
        taurusStars.add(testStars.get(0));

        testConstellations = List.of(
                new Constellation("Taurus", taurusStars)
        );
    }

    @Test
    void testSaveStarsAndConstellations() throws IOException {
        DataWriter.saveStarsAndConstellations(testFilePath.toString(), testStars, testConstellations);

        String fileContent = Files.readString(testFilePath);

        assertTrue(fileContent.contains("Sirius"));
        assertTrue(fileContent.contains("Canopus"));

        assertTrue(fileContent.contains("Taurus"));
    }
}