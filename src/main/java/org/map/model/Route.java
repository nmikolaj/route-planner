package org.map.model;

import java.util.List;

public class Route {
    final private String name;
    final private List<Point> points;

    public Route(String name, List<Point> points) {
        this.name = name;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public List<Point> getPoints() {
        return points;
    }

    public boolean contains(Point point) {
        return points.contains(point);
    }

    public void addPoint(Point point) {
        if (!points.contains(point)) {
            points.add(point);
        }
    }

    public void removePoint(Point point) {
        points.remove(point);
    }
}
