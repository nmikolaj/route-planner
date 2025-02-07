package org.map.model;

public class Point {
    private String name;
    private double xPosition;
    private double yPosition;
    private double size;

    public Point(String name, double xPosition, double yPosition, double size) {
        this.name = name;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) { this.name = newName; }

    public double getXPosition() {
        return xPosition;
    }

    public void setXPosition(double xPosition) { this.xPosition = xPosition; }

    public double getYPosition() {
        return yPosition;
    }

    public void setYPosition(double yPosition) { this.yPosition = yPosition; }

    public double getSize() {
        return size;
    }

    public void setSize(double newSize) { this.size = newSize; }
}
