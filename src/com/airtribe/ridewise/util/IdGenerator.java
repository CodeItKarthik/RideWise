package com.airtribe.ridewise.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private static final AtomicLong riderIdCounter = new AtomicLong(1000);
    private static final AtomicLong driverIdCounter = new AtomicLong(2000);
    private static final AtomicLong rideIdCounter = new AtomicLong(3000);

    public static String generateRiderId() {
        return "RIDER_" + riderIdCounter.incrementAndGet();
    }

    public static String generateDriverId() {
        return "DRIVER_" + driverIdCounter.incrementAndGet();
    }

    public static String generateRideId() {
        return "RIDE_" + rideIdCounter.incrementAndGet();
    }
}
