package org.starling.lghrunningapp;

public class RoutePoint {
    private double latitude;
    private double longitude;
    private long timstamp;
    public long nrOfPoints;

    public RoutePoint(double latitude, double longitude, long time) {
        this.timstamp = time;
        this.latitude = latitude;
        this.longitude = longitude;

    }
}
