import { Component } from '@angular/core';
import { ReportService } from '../../service/report.service';
import { ReportReceptionistDTO } from '../../shared/dto/reportreceptionist';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-report-receptionist',
  imports: [CommonModule, FormsModule],
  templateUrl: './report-receptionist.component.html',
  styleUrl: './report-receptionist.component.css'
})
export class ReportReceptionistComponent {
  loading      = false;
  exporting    = false;
  hasGenerated = false;
  data: ReportReceptionistDTO[] = [];

  start = this.firstDayOfMonth();
  end   = this.today();

  constructor(private reportService: ReportService) {}

  get totalAttended():  number { return this.data.reduce((a, r) => a + r.totalAttended, 0); }
  get totalConfirmed(): number { return this.data.reduce((a, r) => a + r.confirmed, 0); }
  get totalCompleted(): number { return this.data.reduce((a, r) => a + r.completed, 0); }

  generateReport(): void {
    if (!this.start || !this.end) return;
    this.loading      = true;
    this.hasGenerated = false;
    this.data         = [];

    this.reportService.getReceptionistsReport(this.start, this.end).subscribe({
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
    this.reportService.downloadReceptionistsPdf(this.start, this.end).subscribe({
      next: (blob) => {
        this.reportService.saveFile(blob, `receptionists-${this.start}-${this.end}.pdf`);
        this.exporting = false;
      },
      error: () => { this.exporting = false; },
    });
  }

  exportExcel(): void {
    this.exporting = true;
    this.reportService.downloadReceptionistsExcel(this.start, this.end).subscribe({
      next: (blob) => {
        this.reportService.saveFile(blob, `receptionists-${this.start}-${this.end}.xlsx`);
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

  getApprovalRate(r: ReportReceptionistDTO): number {
    if (!r.totalAttended) return 0;
    return Math.round((r.confirmed / r.totalAttended) * 100);
  }

  getRateColorClass(rate: number): string {
    if (rate >= 75) return 'fill-green';
    if (rate >= 50) return 'fill-amber';
    return 'fill-red';
  }

  getRankClass(index: number): string {
    if (index === 0) return 'rank-top';
    if (index === 1) return 'rank-good';
    return 'rank-avg';
  }

  getRankLabel(index: number): string {
    if (index === 0) return '🥇 Top';
    if (index === 1) return '🥈 2nd';
    if (index === 2) return '🥉 3rd';
    return `#${index + 1}`;
  }

  private today(): string {
    return new Date().toISOString().split('T')[0];
  }

  private firstDayOfMonth(): string {
    const d = new Date();
    return new Date(d.getFullYear(), d.getMonth(), 1).toISOString().split('T')[0];
  }
}