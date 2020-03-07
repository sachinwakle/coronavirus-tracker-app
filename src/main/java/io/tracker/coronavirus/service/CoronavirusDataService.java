package io.tracker.coronavirus.service;

import java.util.List;

import io.tracker.coronavirus.model.LocationStat;

public interface CoronavirusDataService {
    List<LocationStat> getAllLocationStats();
}
