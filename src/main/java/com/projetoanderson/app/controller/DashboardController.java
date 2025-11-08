package com.projetoanderson.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projetoanderson.app.dto.DashboardResponseDTO;
import com.projetoanderson.app.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard() {
        DashboardResponseDTO dashboardData = dashboardService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }
}