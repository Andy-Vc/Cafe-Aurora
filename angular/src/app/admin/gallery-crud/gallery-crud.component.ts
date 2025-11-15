// gallery-crud.component.ts
import { Component, OnInit } from '@angular/core';
import { Gallery } from '../../shared/model/gallery.model';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { GalleryService } from '../../service/gallery.service';
import { CommonModule } from '@angular/common';
import { AlertService } from '../../shared/util/sweet-alert';
declare var bootstrap: any;

@Component({
  selector: 'app-gallery-crud',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './gallery-crud.component.html',
  styleUrl: './gallery-crud.component.css',
})
export class GalleryCrudComponent implements OnInit {
  galleries: Gallery[] = [];
  filteredGalleries: Gallery[] = [];
  paginatedGalleries: Gallery[] = []; // ⬅️ NUEVO
  galleryForm!: FormGroup;
  selectedGallery: Gallery | null = null;
  selectedFile?: File;
  previewUrl?: string;
  isEditMode = false;
  loading = false;
  Math = Math; 
  searchTerm = '';
  filterFeatured = 'all';
  filterVisible = 'all';

  currentPage = 1;
  pageSize = 12; 
  totalPages = 0;
  pageSizeOptions = [8, 12, 16, 24];

  // Modales
  private galleryModal: any;

  constructor(
    private fb: FormBuilder,
    private galleryService: GalleryService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadGalleries();
    this.initModals();
  }

  initForm(): void {
    this.galleryForm = this.fb.group({
      idGallery: [0],
      title: [''],
      description: [''],
      imageUrl: [''],
      featured: [false],
      isVisible: [true],
    });
  }

  initModals(): void {
    const galleryModalEl = document.getElementById('galleryModal');

    if (galleryModalEl) {
      this.galleryModal = new bootstrap.Modal(galleryModalEl);
    }
  }

  loadGalleries(): void {
    this.galleryService.getAllGallery();
    this.galleryService.galleries$.subscribe({
      next: (data) => {
        this.galleries = data;
        this.applyFilters();
      },
      error: (err) => {
        console.error('Error al cargar galerías:', err);
        AlertService.error('Error al cargar las galerías');
      },
    });
  }

  applyFilters(): void {
    let filtered = [...this.galleries];

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(
        (g) =>
          g.title?.toLowerCase().includes(term) ||
          g.description?.toLowerCase().includes(term)
      );
    }

    if (this.filterFeatured === 'featured') {
      filtered = filtered.filter((g) => g.featured);
    } else if (this.filterFeatured === 'normal') {
      filtered = filtered.filter((g) => !g.featured);
    }

    if (this.filterVisible === 'visible') {
      filtered = filtered.filter((g) => g.isVisible);
    } else if (this.filterVisible === 'hidden') {
      filtered = filtered.filter((g) => !g.isVisible);
    }

    this.filteredGalleries = filtered;
    
    this.currentPage = 1; 
    this.updatePagination();
  }

  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredGalleries.length / this.pageSize);
    
    if (this.currentPage > this.totalPages && this.totalPages > 0) {
      this.currentPage = this.totalPages;
    }
    
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.paginatedGalleries = this.filteredGalleries.slice(startIndex, endIndex);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
      window.scrollTo({ top: 0, behavior: 'smooth' });
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

  // Resto de métodos existentes...
  openModal(gallery?: Gallery): void {
    this.isEditMode = !!gallery;
    this.selectedGallery = gallery || null;
    this.selectedFile = undefined;
    this.previewUrl = undefined;

    if (gallery) {
      this.galleryForm.patchValue({
        idGallery: gallery.idGallery,
        title: gallery.title || '',
        description: gallery.description || '',
        imageUrl: gallery.imageUrl,
        featured: gallery.featured || false,
        isVisible: gallery.isVisible !== false,
      });
    } else {
      this.galleryForm.reset({
        featured: false,
        isVisible: true,
      });
    }

    this.galleryModal.show();
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      AlertService.info('Por favor selecciona una imagen válida');
      event.target.value = '';
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      AlertService.info('La imagen no debe superar 5MB');
      event.target.value = '';
      return;
    }

    this.selectedFile = file;

    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.previewUrl = e.target.result;
    };
    reader.readAsDataURL(file);
  }

  removeImage(): void {
    this.selectedFile = undefined;
    this.previewUrl = undefined;
    const fileInput = document.querySelector(
      'input[type="file"]'
    ) as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  onSubmit(): void {
    if (this.loading) return;

    if (!this.isEditMode && !this.selectedFile) {
      AlertService.info('Debes seleccionar una imagen');
      return;
    }

    this.loading = true;

    const galleryData: Gallery = {
      idGallery: this.galleryForm.value.idGallery || null,
      title: this.galleryForm.value.title,
      description: this.galleryForm.value.description,
      imageUrl: this.galleryForm.value.imageUrl || '',
      featured: this.galleryForm.value.featured,
      isVisible: this.galleryForm.value.isVisible,
    };

    const request$ = this.isEditMode
      ? this.galleryService.updateGallery(galleryData, this.selectedFile)
      : this.galleryService.createGallery(galleryData, this.selectedFile);

    request$.subscribe({
      next: (response) => {
        this.loading = false;
        if (response.value) {
          AlertService.success(response.message);
          this.galleryModal.hide();
          this.loadGalleries();
        } else {
          AlertService.error(response.message || 'Error al guardar');
        }
      },
      error: (error) => {
        this.loading = false;
        AlertService.error('Error:', error || 'Error al guardar la imagen');
      },
    });
  }

  toggleFeatured(gallery: Gallery): void {
    this.galleryService.featureGallery(gallery.idGallery).subscribe({
      next: (response) => {
        if (response.value) {
          AlertService.success(response.message);
          this.loadGalleries();
        } else {
          AlertService.error(response.message);
        }
      },
      error: (error) => {
        AlertService.error(
          'Error:',
          error || 'Error al cambiar estado destacado'
        );
      },
    });
  }

  deleteGallery(gallery: Gallery): void {
    AlertService.confirm(
      `¿Deseas ${gallery.isVisible ? 'desactivar' : 'activar'} esta imagen?`
    ).then((result) => {
      if (result.isConfirmed) {
        this.galleryService.deleteGallery(gallery.idGallery).subscribe({
          next: (response) => {
            if (response.value) {
              AlertService.success(response.message);
              this.loadGalleries();
            } else {
              AlertService.error(response.message);
            }
          },
          error: () => {
            AlertService.error('Error al eliminar la imagen.');
          },
        });
      }
    });
  }

  onImageError(event: any): void {
    event.target.src = 'assets/images/placeholder.jpg';
  }
}