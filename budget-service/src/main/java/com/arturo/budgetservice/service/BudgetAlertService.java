package com.arturo.budgetservice.service;

import com.arturo.budgetservice.dto.response.BudgetAlertDTO;

import java.util.List;

public interface BudgetAlertService {
    
    List<BudgetAlertDTO> getAllAlerts(Long userId);
    
    List<BudgetAlertDTO> getUnreadAlerts(Long userId);
    
    void markAlertAsRead(Long alertId, Long userId);
    
    void markAllAlertsAsRead(Long userId);
    
    long getUnreadAlertsCount(Long userId);
}