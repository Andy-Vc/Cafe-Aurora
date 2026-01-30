package com.cafeAurora.controller;

import com.cafeAurora.model.TableCoffe;
import com.cafeAurora.service.TableCoffeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/tablecoffe")
@RequiredArgsConstructor
public class TableCoffeController {
    private final TableCoffeService service;

    @GetMapping("/tables/available")
    public List<TableCoffe> getAvailableTables(
            @RequestParam @DateTimeFormat(pattern = "HH:mm[:ss]") LocalTime time,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        return service.getAvailableTables(date, time);
    }
}
