package org.starling.lghrunningapp;

public class RoutePoint {
    private double latitude;
    private double longitude;
    private long timstamp;
    public long nrOfPoints;
    public float speed;

    public RoutePoint(double latitude, double longitude, long time, float speed) {
        this.timstamp = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;


    }
}
