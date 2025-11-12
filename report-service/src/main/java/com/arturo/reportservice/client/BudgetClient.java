package com.arturo.reportservice.client;

import com.arturo.reportservice.dto.BudgetSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "budget-service")
public interface BudgetClient {
    
    @GetMapping("/budgets/summary")
    BudgetSummaryDTO getBudgetSummary(@RequestHeader("X-User-Id") Long userId);
}