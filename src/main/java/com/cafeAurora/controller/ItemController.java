package com.cafeAurora.controller;

import org.springframework.http.MediaType;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.Item;
import com.cafeAurora.service.ItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/list")
	public List<Item> listAllitems(){
		return itemService.getAllItems();
	}
	
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> createItem(
        @RequestPart("item") Item item,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            ResultResponse result = itemService.createItem(item, image);

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
	public ResponseEntity<?> getItem(@PathVariable("id") Integer id) {
		try {
			Item getItem = itemService.getOne(id);
			if (getItem == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ningun producto con ID: " + id);
			}
			return ResponseEntity.ok(getItem);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener producto con ID: " + id);
		}
	}

	@PatchMapping(value = "/update",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResultResponse> updateCategory(@RequestPart("item") Item item, @RequestPart(value = "image", required = false) MultipartFile image) {
		try {
			ResultResponse result = itemService.updateItem(item, image);
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
	public ResponseEntity<ResultResponse> deleteItem(@PathVariable("id") Integer id) {
		try {
			ResultResponse result = itemService.deleteItem(id);
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
	
	@PutMapping("/feature/{id}")
	public ResponseEntity<ResultResponse> favoriteItem(@PathVariable("id") Integer id) {
	    try {
	        ResultResponse result = itemService.featureItem(id);
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
