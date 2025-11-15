import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Category } from '../../shared/model/category.model';
import { Item } from '../../shared/model/item.model';
import { CategoryService } from '../../service/category.service';
import { ItemService } from '../../service/item.service';
import { Gallery } from '../../shared/model/gallery.model';
import { GalleryService } from '../../service/gallery.service';
declare var bootstrap: any;
interface TeamMember {
  name: string;
  role: string;
  image: string;
}
@Component({
  selector: 'app-index',
  imports: [CommonModule],
  templateUrl: './index.component.html',
  styleUrl: './index.component.css',
})
export class IndexComponent implements OnInit {
  categories: Category[] = [];
  images: Gallery[] = [];
  activeCategoryId: number | null = null;
  items: Item[] = [];
  selectedImage: number | null = null;

  constructor(
    private categoryService: CategoryService,
    private itemService: ItemService,
    private galleryService: GalleryService
  ) {}

  ngOnInit(): void {
    this.categoryService.categories$.subscribe((cats) => {
      if (cats.length > 0) {
        this.categories = cats;
        this.categories = cats.slice(0, 4);
        if (!this.activeCategoryId) {
          this.setActiveCategory(cats[0].idCat);
        }
      }
    });
    this.loadFeaturedGallery();
    this.categoryService.allCategoriesActives();
  }

  setActiveCategory(idCat: number): void {
    this.activeCategoryId = idCat;

    this.itemService.getFeaturedItemsByCategory(idCat).subscribe({
      next: (data) => {
        this.items = data;
      },
      error: () => {
        this.items = [];
      },
    });
  }

  isActive(idCat: number): boolean {
    return this.activeCategoryId === idCat;
  }

  loadFeaturedGallery(): void {
    this.galleryService.getAllGalleryFeatured().subscribe({
      next: (data) => {
        this.images = data;
      },
      error: (err) => {
        console.error('Error al cargar galería destacada:', err);
      },
    });
  }

  openImage(index: number): void {
    this.selectedImage = index;

    setTimeout(() => {
      const modalElement = document.getElementById('imageModal');
      if (modalElement) {
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
      }
    });
  }

  closeImage(): void {
    this.selectedImage = null;
  }

  /*default mockup nosotros */
  teamMembers: TeamMember[] = [
    {
      name: 'María García',
      role: 'Barista Master',
      image:
        'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&h=400&fit=crop',
    },
    {
      name: 'Carlos Ruiz',
      role: 'Chef Pastelero',
      image:
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&h=400&fit=crop',
    },
    {
      name: 'Ana Martínez',
      role: 'Gerente',
      image:
        'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400&h=400&fit=crop',
    },
    {
      name: 'Samuel Torres',
      role: 'Tostador Artesanal de Café',
      image:
        'https://images.unsplash.com/photo-1599566150163-29194dcaad36?w=400&h=400&fit=crop',
    },
  ];
}
