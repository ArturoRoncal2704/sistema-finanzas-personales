package com.arturo.reportservice.service;

import com.arturo.reportservice.dto.response.*;

import java.time.LocalDate;

public interface ReportService {
    
    DashboardData getDashboardData(Long userId, LocalDate startDate, LocalDate endDate);
    
    MonthlySummary getMonthlySummary(Long userId, int year, int month);
    
    CategoryAnalysis getCategoryAnalysis(Long userId, String categoryName, 
                                        LocalDate startDate, LocalDate endDate);
    
    ComparisonData comparePeriods(Long userId, 
                                 LocalDate period1Start, LocalDate period1End,
                                 LocalDate period2Start, LocalDate period2End);
}