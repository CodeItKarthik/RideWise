package com.airtribe.ridewise.service;

import com.airtribe.ridewise.model.Rider;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RiderService {
    private Map<String, Rider> riders = new HashMap<>();

    public void registerRider(Rider rider) {
        riders.put(rider.getId(), rider);
    }

    public Rider getRiderById(String riderId) {
        return riders.get(riderId);
    }

    public boolean riderExists(String riderId) {
        return riders.containsKey(riderId);
    }

    public Collection<Rider> getAllRiders() {
        return riders.values();
    }

    public int getTotalRiders() {
        return riders.size();
    }
}
