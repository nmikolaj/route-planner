package org.starmap.controller;

import org.starmap.model.Constellation;
import org.starmap.model.Star;
import org.starmap.utils.DataWriter;

import java.io.IOException;
import java.util.List;


public class FileManagementController {

    private final StarMapController starMapController;

    public FileManagementController(StarMapController starMapController) {
        this.starMapController = starMapController;
    }

    public void saveData() throws IOException {
        List<Star> stars = starMapController.getStars();
        List<Constellation> constellations = starMapController.getConstellations();

        String filePath = "src/main/resources/saved_stars_and_constellations.json";
        DataWriter.saveStarsAndConstellations(filePath, stars, constellations);
    }
}