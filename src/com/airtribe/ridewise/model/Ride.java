package com.airtribe.ridewise.model;

public class Ride {
    private String id;
    private Rider rider;
    private Driver driver;
    private double distance;
    private double fare;
    private RideStatus status;
    private long createdAt;

    public Ride(String id, Rider rider, double distance) {
        this.id = id;
        this.rider = rider;
        this.distance = distance;
        this.status = RideStatus.REQUESTED;
        this.fare = 0.0;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public Rider getRider() {
        return rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public double getDistance() {
        return distance;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "id='" + id + '\'' +
                ", rider=" + rider.getName() +
                ", driver=" + (driver != null ? driver.getName() : "Unassigned") +
                ", distance=" + distance +
                ", fare=" + fare +
                ", status=" + status +
                '}';
    }
}
