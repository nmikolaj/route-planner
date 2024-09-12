package org.starmap.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.starmap.model.Constellation;
import org.starmap.model.Star;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataWriter {

    public static void saveStarsAndConstellations(String filePath, List<Star> stars, List<Constellation> constellations) throws IOException {
        JSONObject jsonObject = new JSONObject();
        JSONArray starsJson = new JSONArray();

        for (Star star : stars) {
            JSONObject starJson = new JSONObject();
            starJson.put("name", star.getName());
            starJson.put("xPosition", star.getXPosition());
            starJson.put("yPosition", star.getYPosition());
            starJson.put("brightness", star.getBrightness());
            starsJson.put(starJson);
        }

        jsonObject.put("stars", starsJson);

        JSONArray constellationsJson = new JSONArray();

        for (Constellation constellation : constellations) {
            JSONObject constellationJson = new JSONObject();
            JSONArray starNames = new JSONArray();

            for (Star star : constellation.getStars()) {
                starNames.put(star.getName());
            }

            constellationJson.put("name", constellation.getName());
            constellationJson.put("stars", starNames);
            constellationsJson.put(constellationJson);
        }

        jsonObject.put("constellations", constellationsJson);
        writeToFile(filePath, jsonObject.toString(4));
    }

    private static void writeToFile(String filePath, String content) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
        }
    }
}