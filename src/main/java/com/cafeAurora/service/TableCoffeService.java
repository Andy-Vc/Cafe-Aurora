package com.cafeAurora.service;

import com.cafeAurora.dto.ResultResponse;
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
        return repository.findAvailableTables(date, time);
    }
    
    /* Crud Service */
	public List<TableCoffe> getAllTables() {
		return repository.findAllByOrderByIdTableAsc();
	}

	public TableCoffe getOne(Integer id) {
		return repository.findById(id).orElseThrow();
	}

	public ResultResponse createTableCoffe(TableCoffe tableCoffe) {
	    try {
	        if (repository.findByTableNumber(tableCoffe.getTableNumber()) != null) {
	            return new ResultResponse(false,
	                "El número de mesa " + tableCoffe.getTableNumber() + " ya existe en el sistema");
	        }

	        repository.save(tableCoffe);
	        return new ResultResponse(true, 
	            "Mesa " + tableCoffe.getTableNumber() + " registrada correctamente");

	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResultResponse(false, e.getMessage());
	    }
	}

	public ResultResponse updateTableCoffe(TableCoffe tableCoffe) {
	    try {
	        TableCoffe existing = repository.findByTableNumber(tableCoffe.getTableNumber());

	        if (existing != null && !existing.getIdTable().equals(tableCoffe.getIdTable())) {
	            return new ResultResponse(false,
	                "El número de mesa " + tableCoffe.getTableNumber() + " ya está usado por otra mesa");
	        }

	        repository.save(tableCoffe);
	        return new ResultResponse(true, 
	            "Mesa " + tableCoffe.getTableNumber() + " actualizada correctamente");

	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResultResponse(false, e.getMessage());
	    }
	}

	public ResultResponse deleteTableCoffe(Integer id) {
		TableCoffe category = repository.findById(id).orElseThrow();
		String action = category.getIsAvailable() ? "Desactivado" : "Activado";
		category.setIsAvailable(!category.getIsAvailable());

		try {
			TableCoffe deleted = repository.save(category);
			String message = String.format("Mesa %s ha sido %s correctamente", deleted.getTableNumber(), action.toLowerCase());
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}
}
