import { Component, OnInit } from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Category } from '../../shared/model/category.model';
import { CategoryService } from '../../service/category.service';
import { AlertService } from '../../shared/util/sweet-alert';

@Component({
  selector: 'app-list',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './category-crud.component.html',
  styleUrl: './category-crud.component.css',
})
export class CategoryCrudComponent implements OnInit {
  categories: Category[] = [];
  filteredCategories: Category[] = [];
  searchTerm: string = '';
  loading: boolean = false;

  // Paginación
  currentPage: number = 1;
  itemsPerPage: number = 10;
  totalPages: number = 0;

  // Modal
  showModal: boolean = false;
  modalMode: 'view' | 'edit' | 'create' = 'view';
  selectedCategory: Category | null = null;

  constructor(private categoryService: CategoryService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loading = true;
    this.categoryService.categories$.subscribe({
      next: (data) => {
        this.categories = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching categories', err);
        this.loading = false;
      },
    });
    this.categoryService.allCategories();
  }

  applyFilters(): void {
    let filtered = [...this.categories];

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(
        (cat) =>
          cat.nameCat.toLowerCase().includes(term) ||
          cat.description?.toLowerCase().includes(term)
      );
    }

    this.filteredCategories = filtered;
    this.totalPages = Math.ceil(
      this.filteredCategories.length / this.itemsPerPage
    );
    this.currentPage = 1;
  }

  get paginatedCategories(): Category[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredCategories.slice(start, end);
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

  openCreateModal(): void {
    this.modalMode = 'create';
    this.selectedCategory = {
      nameCat: '',
      description: '',
      isActive: true,
    } as Category;
    this.showModal = true;
  }

  openViewModal(category: Category): void {
    this.modalMode = 'view';
    this.selectedCategory = { ...category };
    this.showModal = true;
  }

  openEditModal(category: Category): void {
    this.modalMode = 'edit';
    this.selectedCategory = { ...category };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedCategory = null;
  }

  saveCategory(): void {
    if (!this.selectedCategory) return;

    if (this.modalMode === 'create') {
      this.categoryService.createCategory(this.selectedCategory).subscribe({
        next: (created) => {
          AlertService.success(created.message);
          this.loadCategories();
          this.closeModal();
        },
        error: (err) => {
          const errorMessage =
            err.error?.message ||
            err.error?.error?.message ||
            'Ha ocurrido un error';
          AlertService.error(errorMessage);
          console.error('Error creando categoría', err);
        },
      });
    } else if (this.modalMode === 'edit') {
      this.categoryService.updateCategory(this.selectedCategory).subscribe({
        next: (update) => {
          AlertService.success(update.message);
          this.loadCategories();
          this.closeModal();
        },
        error: (err) => {
          const errorMessage =
            err.error?.message ||
            err.error?.error?.message ||
            'Ha ocurrido un error';
          AlertService.error(errorMessage);
          console.error('Error actualizando categoría', err);
        },
      });
    }
  }

  toggleStatus(category: Category, event: Event): void {
    event.stopPropagation();

    const input = event.target as HTMLInputElement;
    input.checked = category.isActive;

    AlertService.confirm(
      `¿Deseas ${category.isActive ? 'desactivar' : 'activar'} esta categoría?`
    ).then((result) => {
      if (result.isConfirmed) {
        this.categoryService.deleteCategory(category.idCat).subscribe({
          next: (response) => {
            category.isActive = !category.isActive;
            AlertService.success(
              response.message
            );
          },
          error: (err) => {
            console.error('Error al cambiar estado:', err);
            AlertService.error('Error al cambiar el estado de la categoría');
          },
        });
      }
    });
  }
}
