package com.cafeAurora.service;

import com.cafeAurora.model.TableCoffe;
import com.cafeAurora.repository.ITableCoffeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TableCoffeService {
    private final ITableCoffeRepository repository;

    public List<TableCoffe> getAvailableTables(LocalDate date, LocalTime time) {
        LocalTime start = time.minusHours(1);
        LocalTime end   = time.plusHours(1);

        return repository.findAvailableTables(date, start, end);
    }
}
