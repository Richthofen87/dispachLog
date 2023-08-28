package ru.vladimir.sazonov.dispatchLog.model.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StuffInfo {
    private String division;
    private int count;
}
