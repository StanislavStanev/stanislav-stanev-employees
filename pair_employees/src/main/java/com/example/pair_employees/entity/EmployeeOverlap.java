package com.example.pair_employees.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOverlap {

    private int emp1;
    private int emp2;
    private long overlapDays;
}