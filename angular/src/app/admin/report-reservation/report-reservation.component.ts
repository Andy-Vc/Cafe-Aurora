import { Component } from '@angular/core';
import { ReportFilters } from '../../shared/dto/reportfilters';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReportReservationDTO } from '../../shared/dto/reportreservation';
import { ReportService } from '../../service/report.service';

@Component({
  selector: 'app-report-reservation',
  imports: [CommonModule, FormsModule],
  templateUrl: './report-reservation.component.html',
  styleUrl: './report-reservation.component.css',
})
export class ReportReservationComponent {
  loading = false;
  exporting = false;
  hasGenerated = false;
  data: ReportReservationDTO[] = [];

  filters: ReportFilters = {
    start: this.firstDayOfMonth(),
    end: this.today(),
    status: '',
    source: '',
  };

  currentPage = 1;
  pageSize = 10;

  get totalPages(): number {
    return Math.ceil(this.data.length / this.pageSize);
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  get pagedData(): ReportReservationDTO[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.data.slice(start, start + this.pageSize);
  }

  constructor(private reportService: ReportService) {}

  generateReport(): void {
    if (!this.filters.start || !this.filters.end) return;

    this.loading = true;
    this.hasGenerated = false;
    this.data = [];
    this.currentPage = 1;

    const f = this.cleanFilters();

    this.reportService.getReservationsReport(f).subscribe({
      next: (res) => {
        this.data = res;
        this.hasGenerated = true;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.hasGenerated = true;
      },
    });
  }

  exportPdf(): void {
    this.exporting = true;
    this.reportService.downloadReservationsPdf(this.cleanFilters()).subscribe({
      next: (blob) => {
        this.reportService.saveFile(
          blob,
          `reservations-${this.filters.start}-${this.filters.end}.pdf`,
        );
        this.exporting = false;
      },
      error: () => {
        this.exporting = false;
      },
    });
  }

  exportExcel(): void {
    this.exporting = true;
    this.reportService
      .downloadReservationsExcel(this.cleanFilters())
      .subscribe({
        next: (blob) => {
          this.reportService.saveFile(
            blob,
            `reservations-${this.filters.start}-${this.filters.end}.xlsx`,
          );
          this.exporting = false;
        },
        error: () => {
          this.exporting = false;
        },
      });
  }

  resetFilters(): void {
    this.filters = {
      start: this.firstDayOfMonth(),
      end: this.today(),
      status: '',
      source: '',
    };
    this.data = [];
    this.hasGenerated = false;
    this.currentPage = 1;
  }

  prevPage(): void {
    if (this.currentPage > 1) this.currentPage--;
  }
  nextPage(): void {
    if (this.currentPage < this.totalPages) this.currentPage++;
  }
  goToPage(p: number): void {
    this.currentPage = p;
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDIENTE: 'badge-pending',
      CONFIRMADA: 'badge-confirmed',
      RECHAZADA: 'badge-rejected',
      CANCELADA: 'badge-cancelled',
      COMPLETADA: 'badge-completed',
      NO_ASISTIO: 'badge-noshow',
    };
    return map[status] ?? 'badge-pending';
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDIENTE: 'Pendiente',
      CONFIRMADA: 'Confirmada',
      RECHAZADA: 'Rechazada',
      CANCELADA: 'Cancelada',
      COMPLETADA: 'Completada',
      NO_ASISTIO: 'No Asistió',
    };
    return map[status] ?? status;
  }

  private cleanFilters(): ReportFilters {
    return {
      start: this.filters.start,
      end: this.filters.end,
      status: this.filters.status || undefined,
      source: this.filters.source || undefined,
    };
  }

  private today(): string {
    return new Date().toISOString().split('T')[0];
  }

  private firstDayOfMonth(): string {
    const d = new Date();
    return new Date(d.getFullYear(), d.getMonth(), 1)
      .toISOString()
      .split('T')[0];
  }
}
