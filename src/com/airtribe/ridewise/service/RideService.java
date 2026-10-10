package com.airtribe.ridewise.service;

import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.model.Ride;
import com.airtribe.ridewise.model.RideStatus;
import com.airtribe.ridewise.strategy.RideMatchingStrategy;
import com.airtribe.ridewise.strategy.FareStrategy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RideService {
    private Map<String, Ride> rides = new HashMap<>();
    private DriverService driverService;
    private RiderService riderService;
    private RideMatchingStrategy matchingStrategy;
    private FareStrategy fareStrategy;

    public RideService(DriverService driverService, RiderService riderService,
                       RideMatchingStrategy matchingStrategy, FareStrategy fareStrategy) {
        this.driverService = driverService;
        this.riderService = riderService;
        this.matchingStrategy = matchingStrategy;
        this.fareStrategy = fareStrategy;
    }

    public Ride requestRide(Ride ride) throws NoDriverAvailableException {
        List<Driver> availableDrivers = driverService.getAvailableDrivers();
        if (availableDrivers.isEmpty()) {
            throw new NoDriverAvailableException("No drivers available at the moment");
        }

        Driver assignedDriver = matchingStrategy.findDriver(ride.getRider(), availableDrivers);
        if (assignedDriver == null) {
            throw new NoDriverAvailableException("Could not find a suitable driver");
        }

        ride.setDriver(assignedDriver);
        ride.setStatus(RideStatus.ASSIGNED);
        rides.put(ride.getId(), ride);
        driverService.updateDriverAvailability(assignedDriver.getId(), false);
        
        return ride;
    }

    public FareReceipt completeRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride == null) {
            return null;
        }

        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            return null;
        }

        ride.setStatus(RideStatus.COMPLETED);
        double totalFare = fareStrategy.calculateFare(ride);
        ride.setFare(totalFare);

        Driver driver = ride.getDriver();
        driverService.updateDriverAvailability(driver.getId(), true);

        return new FareReceipt(ride.getId(), totalFare);
    }

    public Ride getRideById(String rideId) {
        return rides.get(rideId);
    }

    public void cancelRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride != null && ride.getStatus() == RideStatus.REQUESTED) {
            ride.setStatus(RideStatus.CANCELLED);
        } else if (ride != null && ride.getStatus() == RideStatus.ASSIGNED) {
            ride.setStatus(RideStatus.CANCELLED);
            driverService.updateDriverAvailability(ride.getDriver().getId(), true);
        }
    }

    public Map<String, Ride> getAllRides() {
        return new HashMap<>(rides);
    }
}
