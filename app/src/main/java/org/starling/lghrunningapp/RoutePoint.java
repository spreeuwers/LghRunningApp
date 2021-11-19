package org.starling.lghrunningapp;

public class RoutePoint {
    private double latitude;
    private double longitude;
    private long timestamp;
    public long nrOfPoints;
    public float speed;
    public float accuracy;

    public RoutePoint(double latitude, double longitude, long time, float speed, float accuracy) {
        this.timestamp = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.accuracy = accuracy;


    }
}
