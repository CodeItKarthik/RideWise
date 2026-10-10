package com.airtribe.ridewise.model;

public class Driver {
    private String id;
    private String name;
    private String currentLocation;
    private VehicleType vehicleType;
    private boolean available;
    private double rating;

    public Driver(String id, String name, String currentLocation, VehicleType vehicleType) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
        this.vehicleType = vehicleType;
        this.available = true;
        this.rating = 5.0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", currentLocation='" + currentLocation + '\'' +
                ", vehicleType=" + vehicleType +
                ", available=" + available +
                ", rating=" + rating +
                '}';
    }
}
