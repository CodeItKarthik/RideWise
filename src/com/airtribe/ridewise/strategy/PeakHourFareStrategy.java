package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Ride;

public class PeakHourFareStrategy implements FareStrategy {
    private static final double BASE_FARE = 50.0;
    private static final double PER_KM_FARE = 15.0;
    private static final double PEAK_HOUR_MULTIPLIER = 1.2;

    @Override
    public double calculateFare(Ride ride) {
        double fare = BASE_FARE + (ride.getDistance() * PER_KM_FARE);
        return fare * PEAK_HOUR_MULTIPLIER;
    }
}
