package com.cafeAurora.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cafeAurora.dto.ResultResponse;
import com.cafeAurora.model.Item;
import com.cafeAurora.repository.IItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final IItemRepository itemRepository;
	private final CloudinaryService cloudinaryService;

	public List<Item> getAllItems() {
		return itemRepository.findAllByOrderByIdItemAsc();
	}
	
	public List<Item> getFeaturedByCategory(Integer idCategoria){
		return itemRepository.findByCategoryIdCatAndIsFeaturedTrueAndIsAvailableTrueOrderByCreatedAtDesc(idCategoria);
	}
	
	/* Crud Services Item*/
	public Item getOne(Integer id) {
		return itemRepository.findById(id).orElseThrow();
	}

	public ResultResponse createItem(Item item, MultipartFile image) throws IOException {

		if (image != null && !image.isEmpty()) {
			String imageUrl = cloudinaryService.uploadItemImage(image);
			item.setImageUrl(imageUrl);
		}
		try {
			if (itemRepository.findByName(item.getName()) != null) {
				return new ResultResponse(false,
						"El nombre del producto " + item.getName() + " ya existe en el sistema");
			}
			String message = "Producto " + item.getName() + " registrado correctamente";
			itemRepository.save(item);
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public ResultResponse updateItem(Item item, MultipartFile image) throws IOException {
		String oldImageUrl = item.getImageUrl();
		if (image != null && !image.isEmpty()) {
			if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
				cloudinaryService.deleteImage(oldImageUrl);
			}
			String newImageUrl = cloudinaryService.uploadItemImage(image);
			item.setImageUrl(newImageUrl);
		}

		try {
			Item existingItem = itemRepository.findByName(item.getName());
			if (existingItem != null && !existingItem.getIdItem().equals(item.getIdItem())) {
				return new ResultResponse(false,
						"El nombre del producto " + item.getName() + " ya existe en el sistema");
			}

			String message = "Producto " + item.getName() + " actualizado correctamente";
			itemRepository.save(item);
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public ResultResponse deleteItem(Integer id) {
		Item item = itemRepository.findById(id).orElseThrow();
		String action = item.getIsAvailable() ? "Desactivado" : "Activado";
		item.setIsAvailable(!item.getIsAvailable());

		try {
			Item deleted = itemRepository.save(item);
			String message = String.format("%s ha sido %s correctamente", deleted.getName(), action.toLowerCase());
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}

	public ResultResponse featureItem(Integer id) {
		Item item = itemRepository.findById(id).orElseThrow();
		String action = item.getIsFeatured() ? "Eliminado de favoritos" : "Agregado a favoritos";
		item.setIsFeatured(!item.getIsFeatured());

		try {
			Item updated = itemRepository.save(item);
			String message = String.format("%s %s correctamente", updated.getName(), action.toLowerCase());
			return new ResultResponse(true, message);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultResponse(false, e.getMessage());
		}
	}
}