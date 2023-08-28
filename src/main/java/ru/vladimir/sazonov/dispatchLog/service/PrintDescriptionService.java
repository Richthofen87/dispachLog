package ru.vladimir.sazonov.dispatchLog.service;

import ru.vladimir.sazonov.dispatchLog.model.EmergencyTrips;

import java.time.LocalDateTime;
import java.util.List;

public interface PrintDescriptionService {
    void printEmergencyTripDesc(EmergencyTrips emergencyTrip);

    void printTripsList(List<EmergencyTrips> emergencyTrips, LocalDateTime start, LocalDateTime end, String region);
}
