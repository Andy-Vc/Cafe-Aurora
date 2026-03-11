import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TableCoffe } from '../../shared/model/table.model';
import { Subscription } from 'rxjs';
import { TableCoffeService } from '../../service/tablecoffe.service';
import { AlertService } from '../../shared/util/sweet-alert';

@Component({
  selector: 'app-table-crud',
  imports: [CommonModule, FormsModule],
  templateUrl: './table-crud.component.html',
  styleUrl: './table-crud.component.css'
})
export class TableCrudComponent  implements OnInit, OnDestroy {
  allItems: TableCoffe[] = [];
  filteredItems: TableCoffe[] = [];
  paginatedItems: TableCoffe[] = [];

  loading = true;
  saving = false;
  showModal = false;
  isEditing = false;

  searchTerm = '';
  filterLocation: string = 'all';
  filterStatus: 'all' | 'available' | 'unavailable' = 'all';

  currentPage = 1;
  itemsPerPage = 8;
  totalPages = 1;
  pages: number[] = [];

  formTable: Partial<TableCoffe> = this.emptyForm();

  private sub!: Subscription;

  constructor(private tableService: TableCoffeService) {}

  ngOnInit(): void {
    this.sub = this.tableService.tables$.subscribe((data) => {
      this.allItems = data;
      this.applyFilters();
      this.loading = false;
    });
    this.tableService.allTables();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  onSearch(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    const term = this.searchTerm.toLowerCase().trim();

    this.filteredItems = this.allItems.filter((t) => {
      const matchSearch =
        !term ||
        String(t.tableNumber).includes(term) ||
        (t.location?.toLowerCase().includes(term) ?? false);

      const matchLocation =
        this.filterLocation === 'all' || t.location === this.filterLocation;

      const matchStatus =
        this.filterStatus === 'all' ||
        (this.filterStatus === 'available' && t.isAvailable) ||
        (this.filterStatus === 'unavailable' && !t.isAvailable);

      return matchSearch && matchLocation && matchStatus;
    });

    this.currentPage = 1;
    this.buildPagination();
  }

  buildPagination(): void {
    this.totalPages = Math.max(1, Math.ceil(this.filteredItems.length / this.itemsPerPage));
    this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
    this.updatePage();
  }

  updatePage(): void {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    this.paginatedItems = this.filteredItems.slice(start, start + this.itemsPerPage);
  }

  goToPage(page: number): void {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    this.updatePage();
  }

  openCreateModal(): void {
    this.isEditing = false;
    this.formTable = this.emptyForm();
    this.showModal = true;
  }

  openEditModal(table: TableCoffe): void {
    this.isEditing = true;
    this.formTable = { ...table };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  saveTable(): void {
    if (!this.formTable.tableNumber || this.formTable.tableNumber < 1) {
      AlertService.info('El número de mesa es requerido');
      return;
    }
    if (!this.formTable.capacity || this.formTable.capacity < 1) {
      AlertService.info('La capacidad es requerida');
      return;
    }
    if (!this.formTable.location) {
      AlertService.info('Selecciona una ubicación');
      return;
    }

    this.saving = true;

    if (this.isEditing) {
      this.tableService.updateTable(this.formTable as TableCoffe).subscribe({
        next: (res) => {
          AlertService.success(res?.message || 'Mesa actualizada correctamente');
          this.closeModal();
          this.reload();
        },
        error: (err) => {
          AlertService.error(err?.error?.message || 'Error al actualizar la mesa');
          this.saving = false;
        },
      });
    } else {
      this.tableService.createTable(this.formTable as TableCoffe).subscribe({
        next: (res) => {
          AlertService.success(res?.message || 'Mesa creada correctamente');
          this.closeModal();
          this.reload();
        },
        error: (err) => {
          AlertService.error(err?.error?.message || 'Error al crear la mesa');
          this.saving = false;
        },
      });
    }
  }

  toggleStatus(table: TableCoffe, event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    checkbox.checked = table.isAvailable;

    const action = table.isAvailable ? 'deshabilitar' : 'habilitar';

    AlertService.confirm(
      `¿Deseas ${action} la Mesa ${table.tableNumber}?`,
      `La mesa quedará ${table.isAvailable ? 'no disponible' : 'disponible'}.`
    ).then((result) => {
      if (!result.isConfirmed) return; 

      this.tableService.deleteTable(table.idTable).subscribe({
        next: (res) => {
          table.isAvailable = !table.isAvailable; 
          AlertService.success(res?.message || `Mesa ${action === 'habilitar' ? 'habilitada' : 'deshabilitada'}`);
          this.tableService.allTables();
        },
        error: (err) => {
          AlertService.error(err?.error?.message || `Error al ${action} la mesa`);
        },
      });
    });
  }

  getLocationClass(location?: string): string {
    const map: Record<string, string> = {
      Interior: 'loc-interior',
      Terraza:  'loc-terraza',
      VIP:      'loc-vip',
    };
    return map[location ?? ''] ?? 'loc-default';
  }

  private reload(): void {
    this.saving = false;
    this.loading = true;
    this.tableService.allTables();
  }

  private emptyForm(): Partial<TableCoffe> {
    return { tableNumber: undefined, capacity: undefined, location: '' };
  }
}
