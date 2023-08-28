package ru.vladimir.sazonov.dispatchLog.model.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FireStatisticInfo {
    private int countOfFires;
    private int fireDamage;
    private int rescued;
    private int died;
    private int injured;
}
