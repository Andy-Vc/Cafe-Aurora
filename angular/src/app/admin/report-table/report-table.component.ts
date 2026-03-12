import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ReportTableDTO } from '../../shared/dto/reporttable';
import { ReportService } from '../../service/report.service';

@Component({
  selector: 'app-report-table',
  imports: [CommonModule,FormsModule],
  templateUrl: './report-table.component.html',
  styleUrl: './report-table.component.css'
})
export class ReportTableComponent {
  loading      = false;
  exporting    = false;
  hasGenerated = false;
  data: ReportTableDTO[] = [];

  start = this.firstDayOfMonth();
  end   = this.today();

  constructor(private reportService: ReportService) {}

  get totalReservations(): number { return this.data.reduce((a, t) => a + t.totalReservations, 0); }
  get totalConfirmed():    number { return this.data.reduce((a, t) => a + t.confirmed, 0); }
  get totalNoShow():       number { return this.data.reduce((a, t) => a + t.noShow, 0); }

  generateReport(): void {
    if (!this.start || !this.end) return;
    this.loading      = true;
    this.hasGenerated = false;
    this.data         = [];

    this.reportService.getTablesReport(this.start, this.end).subscribe({
      next: (res) => {
        this.data         = res;
        this.hasGenerated = true;
        this.loading      = false;
      },
      error: () => {
        this.loading      = false;
        this.hasGenerated = true;
      },
    });
  }

  exportPdf(): void {
    this.exporting = true;
    this.reportService.downloadTablesPdf(this.start, this.end).subscribe({
      next: (blob) => {
        this.reportService.saveFile(blob, `table-occupation-${this.start}-${this.end}.pdf`);
        this.exporting = false;
      },
      error: () => { this.exporting = false; },
    });
  }

  exportExcel(): void {
    this.exporting = true;
    this.reportService.downloadTablesExcel(this.start, this.end).subscribe({
      next: (blob) => {
        this.reportService.saveFile(blob, `table-occupation-${this.start}-${this.end}.xlsx`);
        this.exporting = false;
      },
      error: () => { this.exporting = false; },
    });
  }

  resetFilters(): void {
    this.start        = this.firstDayOfMonth();
    this.end          = this.today();
    this.data         = [];
    this.hasGenerated = false;
  }

  getUsagePercent(t: ReportTableDTO): number {
    if (!t.totalReservations) return 0;
    return Math.round((t.completed / t.totalReservations) * 100);
  }

  getLocationClass(location: string | null): string {
    const map: Record<string, string> = {
      'Interior': 'loc-interior',
      'Terraza':  'loc-terraza',
      'VIP':      'loc-vip',
    };
    return location ? (map[location] ?? 'loc-default') : 'loc-default';
  }

  private today(): string {
    return new Date().toISOString().split('T')[0];
  }

  private firstDayOfMonth(): string {
    const d = new Date();
    return new Date(d.getFullYear(), d.getMonth(), 1).toISOString().split('T')[0];
  }
}