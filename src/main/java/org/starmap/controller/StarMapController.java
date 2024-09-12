package org.starmap.controller;

import org.starmap.model.Constellation;
import org.starmap.model.Star;
import org.starmap.utils.DataLoader;
import org.starmap.view.StarMapView;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.io.IOException;

public class StarMapController {
    private List<Star> stars;
    private List<Constellation> constellations;

    public StarMapController(String dataFilePath) {
        this.stars = DataLoader.loadStars(dataFilePath);
        this.constellations = DataLoader.loadConstellations(dataFilePath, stars);
    }

    public List<Star> getStars() {
        return stars;
    }

    public void setStars(List<Star> stars) {
        this.stars = stars;
    }

    public List<Constellation> getConstellations() {
        return constellations;
    }

    public List<String> getConstellationNames() {
        List<String> names = new ArrayList<>();
        for (Constellation constellation : constellations) {
            names.add(constellation.getName());
        }
        return names;
    }

    public void setConstellations(List<Constellation> constellations) {
        this.constellations = constellations;
    }

    public void loadStarMap(String filePath, StarMapView view) {
            List<Star> loadedStars = DataLoader.loadStars(filePath);
            List<Constellation> loadedConstellations = DataLoader.loadConstellations(filePath, loadedStars);

            this.setStars(loadedStars);
            this.setConstellations(loadedConstellations);

            view.drawMap();
    }

    // Get star by its name
    public Optional<Star> getStarByName(String name) {
        return stars.stream().filter(star -> star.getName().equalsIgnoreCase(name)).findFirst();
    }
    public void setStarName(Star star, String newName) {
        star.setName(newName);
    }

    public void setStarBrightness(Star star, double newBrightness) {
        star.setBrightness(newBrightness);
    }

    // Get constellation by its name
    public Optional<Constellation> getConstellationByName(String name) {
        return constellations.stream().filter(constellation -> constellation.getName().equalsIgnoreCase(name)).findFirst();
    }

    // Add new star to the map
    public void addStar(StarMapView view) {
        Star star = new Star("New Star", 30, 30, 0);
        stars.add(star);
        // view.clearCanvas();
        view.drawMap();
    }

    // Remove star from the map
    public void removeStar(Star star, StarMapView view) {
        stars.remove(star);

        for (Constellation constellation : constellations) {
            if (constellation.contains(star)) {
                constellation.removeStar(star);
            }
        }
        view.drawMap();
    }

    public void addStarToConstellation(Star star, String newConstellationName, StarMapView view) {
        // Remove star from its current constellation
        for (Constellation constellation : constellations) {
            constellation.removeStar(star);
        }

        constellations.stream()
                .filter(c -> c.getName().equals(newConstellationName))
                .findFirst()
                .ifPresent(constellation -> constellation.addStar(star));
        view.drawMap();
    }

    // Add a new constellation to the map
    public void addConstellation(Constellation constellation) {
        constellations.add(constellation);
    }

    // Remove a constellation from the map
    public void removeConstellation(String name) {
        constellations.removeIf(constellation -> constellation.getName().equalsIgnoreCase(name));
    }
}
