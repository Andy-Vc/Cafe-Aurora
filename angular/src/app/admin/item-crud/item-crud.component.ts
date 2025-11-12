import { Component, OnInit } from '@angular/core';
import { Item } from '../../shared/model/item.model';
import { Category } from '../../shared/model/category.model';
import { ItemService } from '../../service/item.service';
import { CategoryService } from '../../service/category.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AlertService } from '../../shared/util/sweet-alert';

@Component({
  selector: 'app-item-crud',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './item-crud.component.html',
  styleUrl: './item-crud.component.css',
})
export class ItemCrudComponent implements OnInit {
  items: Item[] = [];
  categories: Category[] = [];
  filteredItems: Item[] = [];
  searchTerm: string = '';
  filterCategory: string = 'all';
  filterAvailability: string = 'all';
  loading: boolean = false;

  // Paginación
  currentPage: number = 1;
  itemsPerPage: number = 12;
  totalPages: number = 0;

  // Modal
  showModal: boolean = false;
  modalMode: 'view' | 'edit' | 'create' = 'view';
  selectedItem: Item | null = null;
  selectedImage: File | null = null;
  imagePreview: string | null = null;

  constructor(
    private itemService: ItemService,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadItems();
  }

  loadCategories(): void {
    this.categoryService.categories$.subscribe({
      next: (data) => {
        this.categories = data;
      },
      error: (err) => console.error('Error fetching categories', err),
    });
    this.categoryService.allCategories();
  }

  loadItems(): void {
    this.loading = true;
    this.itemService.items$.subscribe({
      next: (data) => {
        this.items = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching items', err);
        this.loading = false;
      },
    });
    this.itemService.getallItems();
  }

  applyFilters(): void {
    let filtered = [...this.items];

    // Filtro de búsqueda
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(
        (item) =>
          item.name.toLowerCase().includes(term) ||
          item.description?.toLowerCase().includes(term)
      );
    }

    // Filtro por categoría
    if (this.filterCategory !== 'all') {
      filtered = filtered.filter(
        (item) => item.category.idCat.toString() === this.filterCategory
      );
    }

    // Filtro por disponibilidad
    if (this.filterAvailability === 'available') {
      filtered = filtered.filter((item) => item.isAvailable);
    } else if (this.filterAvailability === 'unavailable') {
      filtered = filtered.filter((item) => !item.isAvailable);
    }

    this.filteredItems = filtered;
    this.totalPages = Math.ceil(this.filteredItems.length / this.itemsPerPage);
    this.currentPage = 1;
  }

  get paginatedItems(): Item[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredItems.slice(start, end);
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  onSearch(): void {
    this.applyFilters();
  }

  // CRUD Operations
  openCreateModal(): void {
    this.modalMode = 'create';
    this.selectedItem = {
      category: this.categories[0] || ({} as Category),
      name: '',
      description: '',
      price: 0,
      imageUrl: '',
      isAvailable: true,
      isFeatured: false,
    } as Item;
    this.selectedImage = null;
    this.imagePreview = null;
    this.showModal = true;
  }

  openViewModal(item: Item): void {
    this.modalMode = 'view';
    this.selectedItem = { ...item };
    this.selectedImage = null;
    this.imagePreview = item.imageUrl || null;
    this.showModal = true;
  }

  openEditModal(item: Item): void {
    this.modalMode = 'edit';
    this.selectedItem = { ...item };
    this.selectedImage = null;
    this.imagePreview = item.imageUrl || null;
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedItem = null;
    this.selectedImage = null;
    this.imagePreview = null;
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedImage = input.files[0];

      // Preview
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreview = e.target.result;
      };
      reader.readAsDataURL(this.selectedImage);
    }
  }

  saveItem(): void {
    if (!this.selectedItem) return;

    if (this.modalMode === 'create') {
      this.itemService
        .createItem(this.selectedItem, this.selectedImage || undefined)
        .subscribe({
          next: (created) => {
            AlertService.success(created.message);
            this.loadCategories();
            this.loadItems();
            this.closeModal();
          },
          error: (err) => {
            const errorMessage =
              err.error?.message ||
              err.error?.error?.message ||
              'Ha ocurrido un error';
            AlertService.error(errorMessage);
            console.error('Error creando producto', err);
          },
        });
    } else if (this.modalMode === 'edit') {
      this.itemService
        .updateItem(this.selectedItem, this.selectedImage || undefined)
        .subscribe({
          next: (update) => {
            AlertService.success(update.message);
            this.loadCategories();
            this.loadItems();
            this.closeModal();
          },
          error: (err) => {
            const errorMessage =
              err.error?.message ||
              err.error?.error?.message ||
              'Ha ocurrido un error';
            AlertService.error(errorMessage);
            console.error('Error actualizando producto', err);
          },
        });
    }
  }

  toggleAvailability(item: Item, event: Event): void {
    event.stopPropagation();

    const input = event.target as HTMLInputElement;
    input.checked = item.isAvailable;

    AlertService.confirm(
      `¿Deseas ${item.isAvailable ? 'desactivar' : 'activar'} esta producto?`
    ).then((result) => {
      if (result.isConfirmed) {
        this.itemService.deleteItem(item.idItem).subscribe({
          next: (response) => {
            item.isAvailable = !item.isAvailable;
            AlertService.success(response.message);
          },
          error: (err) => {
            console.error('Error al cambiar estado:', err);
            AlertService.error('Error al cambiar el estado del producto');
          },
        });
      }
    });
  }

  toggleFeatured(item: Item, event: Event): void {
    event.stopPropagation();

    const input = event.target as HTMLInputElement;
    input.checked = item.isFeatured;

    AlertService.confirm(
      `¿Deseas ${
        item.isFeatured ? 'desactivar' : 'activar'
      } el destacado de este producto?`
    ).then((result) => {
      if (result.isConfirmed) {
        this.itemService.featureItem(item.idItem).subscribe({
          next: (response) => {
            if (response.value) {
              item.isFeatured = !item.isFeatured;
              AlertService.success(response.message);
            } else {
              AlertService.error(response.message);
            }
          },
          error: (err) => {
            console.error('Error al cambiar destacado:', err);
            AlertService.error('Error al actualizar el estado de destacado');
          },
        });
      } else {
        input.checked = item.isFeatured;
      }
    });
  }
}
