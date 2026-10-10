package com.airtribe.ridewise.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FareReceipt {
    private String rideId;
    private double amount;
    private String generatedAt;

    public FareReceipt(String rideId, double amount) {
        this.rideId = rideId;
        this.amount = amount;
        this.generatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getRideId() {
        return rideId;
    }

    public double getAmount() {
        return amount;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    @Override
    public String toString() {
        return "FareReceipt{" +
                "rideId='" + rideId + '\'' +
                ", amount=" + amount +
                ", generatedAt=" + generatedAt +
                '}';
    }
}
