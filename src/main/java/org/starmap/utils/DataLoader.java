package org.starmap.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.starmap.model.Constellation;
import org.starmap.model.Star;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    public static List<Star> loadStars(String filePath) {
        List<Star> stars = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray starsJson = jsonObject.getJSONArray("stars");

            for (int i = 0; i < starsJson.length(); i++) {
                JSONObject starJson = starsJson.getJSONObject(i);
                Star star = new Star(
                        starJson.getString("name"),
                        starJson.getDouble("xPosition"),
                        starJson.getDouble("yPosition"),
                        starJson.getDouble("brightness")
                );
                stars.add(star);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stars;
    }

    public static List<Constellation> loadConstellations(String filePath, List<Star> stars) {
        List<Constellation> constellations = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray constellationsJson = jsonObject.getJSONArray("constellations");

            for (int i = 0; i < constellationsJson.length(); i++) {
                JSONObject constellationJson = constellationsJson.getJSONObject(i);
                List<Star> constellationStars = new ArrayList<>();
                JSONArray starNames = constellationJson.getJSONArray("stars");

                for (int j = 0; j < starNames.length(); j++) {
                    String starName = starNames.getString(j);
                    stars.stream()
                            .filter(star -> star.getName().equals(starName))
                            .findFirst()
                            .ifPresent(constellationStars::add);
                }
                Constellation constellation = new Constellation(
                        constellationJson.getString("name"),
                        constellationStars
                );
                constellations.add(constellation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return constellations;
    }
}
