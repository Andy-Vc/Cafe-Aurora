import { Component, OnInit } from '@angular/core';
import { Gallery } from '../../shared/model/gallery.model';
import { GalleryService } from '../../service/gallery.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
declare var bootstrap: any;

@Component({
  selector: 'app-gallery',
  imports: [CommonModule, FormsModule],
  templateUrl: './gallery.component.html',
  styleUrl: './gallery.component.css'
})
export class GalleryComponent implements OnInit {
  allGalleries: Gallery[] = [];
  paginatedGalleries: Gallery[] = [];
  Math = Math;

  // Estados
  loading = false;
  selectedGallery: Gallery | null = null;
  selectedIndex = 0;
  
  // Búsqueda
  searchTerm = '';
  
  // Paginación
  currentPage = 1;
  pageSize = 12;
  totalPages = 0;
  pageSizeOptions = [8, 12, 16, 24];
  
  // Modal
  private galleryModal: any;

  constructor(private galleryService: GalleryService) {}

  ngOnInit(): void {
    this.loadGalleries();
    this.initModal();
  }

  initModal(): void {
    const modalEl = document.getElementById('galleryModal');
    if (modalEl) {
      this.galleryModal = new bootstrap.Modal(modalEl);
    }
  }

  loadGalleries(): void {
    this.loading = true;
    
    this.galleryService.getAllGalleryVisibles().subscribe({
      next: (data) => {
        this.allGalleries = data;
        this.updatePagination();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar galería:', err);
        this.loading = false;
      }
    });
  }

  applySearch(): void {
    this.currentPage = 1;
    this.updatePagination();
  }

  getFilteredGalleries(): Gallery[] {
    if (!this.searchTerm.trim()) {
      return this.allGalleries;
    }

    const term = this.searchTerm.toLowerCase();
    return this.allGalleries.filter(g =>
      g.title?.toLowerCase().includes(term) ||
      g.description?.toLowerCase().includes(term)
    );
  }

  updatePagination(): void {
    const filtered = this.getFilteredGalleries();
    this.totalPages = Math.ceil(filtered.length / this.pageSize);
    
    if (this.currentPage > this.totalPages && this.totalPages > 0) {
      this.currentPage = this.totalPages;
    }
    
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedGalleries = filtered.slice(startIndex, endIndex);
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

  openGallery(gallery: Gallery, index: number): void {
    const filtered = this.getFilteredGalleries();
    this.selectedGallery = gallery;
    this.selectedIndex = filtered.indexOf(gallery);
    this.galleryModal?.show();
  }

  closeGallery(): void {
    this.galleryModal?.hide();
    this.selectedGallery = null;
  }

  nextImage(): void {
    const filtered = this.getFilteredGalleries();
    if (this.selectedIndex < filtered.length - 1) {
      this.selectedIndex++;
      this.selectedGallery = filtered[this.selectedIndex];
    }
  }

  previousImage(): void {
    if (this.selectedIndex > 0) {
      this.selectedIndex--;
      const filtered = this.getFilteredGalleries();
      this.selectedGallery = filtered[this.selectedIndex];
    }
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.applySearch();
  }

  onImageError(event: any): void {
    event.target.src = 'assets/images/placeholder-cafe.jpg';
  }
}