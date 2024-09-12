package org.starmap.model;

import java.util.List;

// Represents a constellation made up of stars
public class Constellation {
    final private String name;
    final private List<Star> stars;

    public Constellation(String name, List<Star> stars) {
        this.name = name;
        this.stars = stars;
    }

    public String getName() {
        return name;
    }

    public List<Star> getStars() {
        return stars;
    }

    public boolean contains(Star star) {
        return stars.contains(star);
    }

    public void addStar(Star star) {
        if (!stars.contains(star)) {
            stars.add(star);
        }
    }

    public void removeStar(Star star) {
        stars.remove(star);
    }
}
