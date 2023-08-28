package ru.vladimir.sazonov.dispatchLog.model.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FireStatisticObject {
    private int countOfFires;
    private String fireObject;
}
