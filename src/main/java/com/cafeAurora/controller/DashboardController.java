package com.cafeAurora.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafeAurora.dto.DashboardRecepcionist;
import com.cafeAurora.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

	@GetMapping("/receptionist")
    public DashboardRecepcionist getReceptionistDashboard() {
        return dashboardService.getReceptionistDashboard();
    }
}
