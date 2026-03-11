package com.cafeAurora.controller;

import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.TableCoffe;
import com.cafeAurora.service.TableCoffeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    
	/* Crud Controller */
    @GetMapping("/list")
	public List<TableCoffe> listAllTables() {
		return service.getAllTables();
	}

	@PostMapping("/register")
	public ResponseEntity<ResultResponse> createTable(@RequestBody TableCoffe table) {
		try {
			ResultResponse result = service.createTableCoffe(table);
			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse errorResponse = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<?> getTable(@PathVariable("id") Integer id) {
		try {
			TableCoffe getTable = service.getOne(id);
			if (getTable == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("No se encontró ninguna mesa con ID: " + id);
			}
			return ResponseEntity.ok(getTable);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener mesa con ID: " + id);
		}
	}

	@PatchMapping("/update")
	public ResponseEntity<ResultResponse> updateTable(@RequestBody TableCoffe table) {
		try {
			ResultResponse result = service.updateTableCoffe(table);
			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse errorResponse = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResultResponse> deleteTable(@PathVariable("id") Integer id) {
		try {
			ResultResponse result = service.deleteTableCoffe(id);
			if (!result.getValue()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
			}
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			ResultResponse errorResponse = new ResultResponse(false, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
}
