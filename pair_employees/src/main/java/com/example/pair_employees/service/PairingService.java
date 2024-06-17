package com.example.pair_employees.service;

import com.example.pair_employees.Utils.DateUtils;
import com.example.pair_employees.entity.Employee;
import com.example.pair_employees.entity.EmployeeOverlap;
import com.opencsv.CSVReader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class PairingService {

    public String getEmployeesByTimeSpentOnSameProjects(MultipartFile file) {

        Map<String, Long> pairDuration = new HashMap<>();

        try {
            List<Employee> employees = readLineByLine(file);

            if (CollectionUtils.isEmpty(employees)) {
                return "File is empty";
            }

            for (int i = 0; i < employees.size(); i++) {
                for (int j = i + 1; j < employees.size(); j++) {
                    Employee ep1 = employees.get(i);
                    Employee ep2 = employees.get(j);

                    if (ep1.getProjectId() == ep2.getProjectId()) {
                        LocalDate start = ep1.getDateFrom().isAfter(ep2.getDateFrom()) ? ep1.getDateFrom() : ep2.getDateFrom();
                        LocalDate end = ep1.getDateTo().isBefore(ep2.getDateTo()) ? ep1.getDateTo() : ep2.getDateTo();

                        if (start.isBefore(end) || start.isEqual(end)) {
                            long days = ChronoUnit.DAYS.between(start, end);

                            String pairKey = ep1.getEmployeeId() + "," + ep2.getEmployeeId();
                            pairDuration.put(pairKey, pairDuration.getOrDefault(pairKey, 0L) + days);
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        EmployeeOverlap longestOverlap = getLongestOverlap(pairDuration);

        return Optional.ofNullable(longestOverlap)
                .map(value -> String.format("Employees %s and %s worked together for the longest period of %s days.", value.getEmp1(), value.getEmp2(), value.getOverlapDays()))
                .orElse("No overlapping project periods found.");
    }

    private EmployeeOverlap getLongestOverlap(Map<String, Long> pairDuration) {
        EmployeeOverlap longestOverlap = null;

        for (Map.Entry<String, Long> entry : pairDuration.entrySet()) {
            String[] empIds = entry.getKey().split(",");
            int emp1 = Integer.parseInt(empIds[0]);
            int emp2 = Integer.parseInt(empIds[1]);
            long days = entry.getValue();

            if (longestOverlap == null || days > longestOverlap.getOverlapDays()) {
                longestOverlap = new EmployeeOverlap(emp1, emp2, days);
            }
        }

        return longestOverlap;
    }

    private List<Employee> readLineByLine(MultipartFile file) throws Exception {
        List<Employee> list = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] line;

                while ((line = csvReader.readNext()) != null) {
                    if (StringUtils.containsAny(line[0], "EmpID", "ProjectID", "DateFrom", "DateTo")) {
                        continue;
                    }

                    list.add(Employee.builder()
                                    .employeeId(Integer.valueOf(line[0]))
                                    .projectId(Integer.valueOf(line[1]))
                                    .dateFrom(DateUtils.getIsoDate(line[2]))
                                    .dateTo(DateUtils.getIsoDate(line[3]))
                            .build());
                }
            }
        }

        return list;
    }
}


