package org.starmap.model;

public class Star {
    private String name;
    private double xPosition;
    private double yPosition;
    private double brightness;

    public Star(String name, double xPosition, double yPosition, double brightness) {
        this.name = name;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.brightness = brightness;
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

    public double getBrightness() {
        return brightness;
    }

    public void setBrightness(double newBrightness) { this.brightness = newBrightness; }
}
