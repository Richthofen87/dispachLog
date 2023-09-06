package ru.vladimir.sazonov.dispatchLog.service;

import java.time.LocalDate;
import java.util.*;

public interface PrintDutyTripsService {
    Map<String, Object> printDutyTrips(LocalDate date);
}
