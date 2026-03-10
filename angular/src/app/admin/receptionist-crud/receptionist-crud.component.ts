import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { User } from '../../shared/model/user.model';
import { Subscription } from 'rxjs';
import { UserService } from '../../service/user.service';
import { AlertService } from '../../shared/util/sweet-alert';

@Component({
  selector: 'app-receptionist-crud',
  imports: [CommonModule, FormsModule],
  templateUrl: './receptionist-crud.component.html',
  styleUrl: './receptionist-crud.component.css',
})
export class ReceptionistCrudComponent implements OnInit, OnDestroy {
  allItems: User[] = [];
  filteredItems: User[] = [];
  paginatedItems: User[] = [];

  loading = true;
  saving = false;
  showModal = false;
  showPassword = false;

  searchTerm = '';
  filterStatus: 'all' | 'active' | 'inactive' = 'all';

  currentPage = 1;
  itemsPerPage = 8;
  totalPages = 1;
  pages: number[] = [];

  newUser: Partial<User> & { password?: string } = this.emptyForm();

  private sub!: Subscription;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.sub = this.userService.receptionist$.subscribe((data) => {
      this.allItems = data;
      this.applyFilters();
      this.loading = false;
    });

    this.userService.allReceptionist();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  onSearch(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    const term = this.searchTerm.toLowerCase().trim();

    this.filteredItems = this.allItems.filter((u) => {
      const matchSearch =
        !term ||
        u.name.toLowerCase().includes(term) ||
        u.email.toLowerCase().includes(term) ||
        u.phone.includes(term);

      const matchStatus =
        this.filterStatus === 'all' ||
        (this.filterStatus === 'active' && u.isActive) ||
        (this.filterStatus === 'inactive' && !u.isActive);

      return matchSearch && matchStatus;
    });

    this.currentPage = 1;
    this.buildPagination();
  }

  buildPagination(): void {
    this.totalPages = Math.max(
      1,
      Math.ceil(this.filteredItems.length / this.itemsPerPage),
    );
    this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
    this.updatePage();
  }

  updatePage(): void {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    this.paginatedItems = this.filteredItems.slice(
      start,
      start + this.itemsPerPage,
    );
  }

  goToPage(page: number): void {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    this.updatePage();
  }

  openCreateModal(): void {
    this.newUser = this.emptyForm();
    this.showPassword = false;
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  createUser(): void {
    if (!this.newUser.name?.trim()) {
      AlertService.info('El nombre es requerido');
      return;
    }
    if (!this.newUser.email?.trim()) {
      AlertService.info('El email es requerido');
      return;
    }
    if (!this.newUser.phone?.trim()) {
      AlertService.info('El teléfono es requerido');
      return;
    }
    if (!this.newUser.password || this.newUser.password.length < 8) {
      AlertService.info('La contraseña debe tener al menos 8 caracteres');
      return;
    }

    this.saving = true;
    this.userService.createReceptionist(this.newUser as User).subscribe({
      next: (res: any) => {
        AlertService.success(
          res?.message || 'Recepcionista creado correctamente',
        );
        this.closeModal();
        this.loading = true;
        this.userService.allReceptionist();
        this.saving = false;
      },
      error: (err) => {
        AlertService.error(
          err?.error?.message || 'Error al crear el recepcionista',
        );
        console.log(err);
        this.saving = false;
      },
    });
  }

  toggleStatus(user: User, event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    checkbox.checked = user.isActive;

    const action = user.isActive ? 'desactivar' : 'activar';

    AlertService.confirm(
      `¿Deseas ${action} a ${user.name}?`,
      `El recepcionista quedará ${user.isActive ? 'inactivo' : 'activo'}.`,
    ).then((result) => {
      if (!result.isConfirmed) return;

      this.userService.deleteReceptionist(user.idUser as string).subscribe({
        next: (res: any) => {
          user.isActive = !user.isActive;
          AlertService.success(
            res?.message ||
              `Recepcionista ${action === 'activar' ? 'activado' : 'desactivado'}`,
          );
          this.userService.allReceptionist();
        },
        error: (err) => {
          AlertService.error(
            err?.error?.message || `Error al ${action} el recepcionista`,
          );
        },
      });
    });
  }
  getInitials(name: string): string {
    if (!name) return '?';
    return name
      .split(' ')
      .slice(0, 2)
      .map((n) => n[0])
      .join('')
      .toUpperCase();
  }

  private emptyForm(): Partial<User> & { password?: string } {
    return { name: '', email: '', phone: '', password: '' };
  }
}
