import { CommonModule } from '@angular/common';
import {
  Component,
  ElementRef,
  HostListener,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Category } from '../../shared/model/category.model';
import { Item } from '../../shared/model/item.model';
import { CategoryService } from '../../service/category.service';
import { ItemService } from '../../service/item.service';

@Component({
  selector: 'app-menu',
  imports: [CommonModule, FormsModule],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css',
})
export class MenuComponent implements OnInit {
  @ViewChild('categoriesContainer') categoriesContainer!: ElementRef;

  categories: Category[] = [];
  allItems: Item[] = [];
  filteredItems: Item[] = [];
  paginatedItems: Item[] = [];
  Math = Math;

  activeCategoryId: number | null = null;
  loading = false;

  searchTerm = '';
  sortBy = 'name';
  showOnlyAvailable = false;
  showOnlyFeatured = false;

  currentPage = 1;
  pageSize = 12;
  totalPages = 0;
  pageSizeOptions = [8, 12, 16, 24];

  canScrollLeft = false;
  canScrollRight = false;

  constructor(
    private categoryService: CategoryService,
    private itemService: ItemService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  ngAfterViewInit() {
    setTimeout(() => this.checkScrollButtons(), 300);
  }

  @HostListener('window:resize')
  onResize() {
    this.checkScrollButtons();
  }

  loadCategories(): void {
    this.categoryService.allCategoriesActives();
    this.categoryService.categories$.subscribe({
      next: (cats) => {
        if (cats.length > 0) {
          this.categories = cats;

          // Seleccionar primera categoría automáticamente
          if (!this.activeCategoryId) {
            this.setActiveCategory(cats[0].idCat);
          }

          setTimeout(() => this.checkScrollButtons(), 200);
        }
      },
      error: (err) => {
        console.error('Error al cargar categorías:', err);
      },
    });
  }

  setActiveCategory(idCat: number): void {
    this.activeCategoryId = idCat;
    this.loading = true;
    this.searchTerm = '';

    this.itemService.getItemsByCategory(idCat).subscribe({
      next: (data) => {
        this.allItems = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar items:', err);
        this.allItems = [];
        this.filteredItems = [];
        this.paginatedItems = [];
        this.loading = false;
      },
    });
  }

  applyFilters(): void {
    let filtered = [...this.allItems];

    // Filtrar por búsqueda
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(
        (item) =>
          item.name.toLowerCase().includes(term) ||
          item.description?.toLowerCase().includes(term)
      );
    }

    // Filtrar solo disponibles
    if (this.showOnlyAvailable) {
      filtered = filtered.filter((item) => item.isAvailable);
    }

    // Filtrar solo destacados
    if (this.showOnlyFeatured) {
      filtered = filtered.filter((item) => item.isFeatured);
    }

    // Ordenar
    switch (this.sortBy) {
      case 'name':
        filtered.sort((a, b) => a.name.localeCompare(b.name));
        break;
      case 'price-asc':
        filtered.sort((a, b) => a.price - b.price);
        break;
      case 'price-desc':
        filtered.sort((a, b) => b.price - a.price);
        break;
    }

    this.filteredItems = filtered;
    this.currentPage = 1;
    this.updatePagination();
  }

  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredItems.length / this.pageSize);

    if (this.currentPage > this.totalPages && this.totalPages > 0) {
      this.currentPage = this.totalPages;
    }

    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedItems = this.filteredItems.slice(startIndex, endIndex);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
      window.scrollTo({ top: 400, behavior: 'smooth' });
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.goToPage(this.currentPage + 1);
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.goToPage(this.currentPage - 1);
    }
  }

  onPageSizeChange(): void {
    this.currentPage = 1;
    this.updatePagination();
  }

  get pageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;

    if (this.totalPages <= maxPagesToShow) {
      for (let i = 1; i <= this.totalPages; i++) {
        pages.push(i);
      }
    } else {
      let startPage = Math.max(1, this.currentPage - 2);
      let endPage = Math.min(this.totalPages, this.currentPage + 2);

      if (this.currentPage <= 3) {
        endPage = maxPagesToShow;
      }

      if (this.currentPage >= this.totalPages - 2) {
        startPage = this.totalPages - maxPagesToShow + 1;
      }

      for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
      }
    }

    return pages;
  }

  get showFirstPage(): boolean {
    return this.pageNumbers[0] > 1;
  }

  get showLastPage(): boolean {
    return this.pageNumbers[this.pageNumbers.length - 1] < this.totalPages;
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.sortBy = 'name';
    this.showOnlyAvailable = false;
    this.showOnlyFeatured = false;
    this.applyFilters();
  }

  isActive(idCat: number): boolean {
    return this.activeCategoryId === idCat;
  }

  // Métodos de scroll (igual que antes)
  scrollCategories(direction: 'left' | 'right') {
    const container = this.categoriesContainer?.nativeElement;
    if (!container) return;

    const containerWidth = container.clientWidth;
    const scrollAmount = Math.min(containerWidth * 0.8, 400);

    const currentScroll = container.scrollLeft;
    const targetScroll =
      direction === 'left'
        ? currentScroll - scrollAmount
        : currentScroll + scrollAmount;

    container.scrollTo({
      left: targetScroll,
      behavior: 'smooth',
    });

    setTimeout(() => this.checkScrollButtons(), 400);
  }

  onScroll() {
    this.checkScrollButtons();
  }

  private checkScrollButtons() {
    const container = this.categoriesContainer?.nativeElement;
    if (!container) {
      this.canScrollLeft = false;
      this.canScrollRight = false;
      return;
    }

    const scrollLeft = Math.ceil(container.scrollLeft);
    const scrollWidth = container.scrollWidth;
    const clientWidth = container.clientWidth;
    const maxScroll = scrollWidth - clientWidth;

    this.canScrollLeft = scrollLeft > 5;
    this.canScrollRight = scrollLeft < maxScroll - 5;
  }

  onImageError(event: any): void {
    event.target.src = 'assets/images/placeholder-cafe.jpg';
  }
}
