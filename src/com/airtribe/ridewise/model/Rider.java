package com.airtribe.ridewise.model;

public class Rider {
    private String id;
    private String name;
    private String location;
    private double rating;

    public Rider(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.rating = 5.0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Rider{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", rating=" + rating +
                '}';
    }
}
