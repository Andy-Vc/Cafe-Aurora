import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { DashboardService } from '../../service/dashboard.service';
import { DashboardAdmin } from '../../shared/dto/dashboardadmin';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit, OnDestroy {
  dashboardData: DashboardAdmin | null = null;
  loading = true;
  today = new Date();

  private lineChart: Chart | null = null;
  private donutChart: Chart | null = null;

  private get isDarkMode(): boolean {
    return document.body.classList.contains('dark-mode');
  }

  constructor(
    private dashboardService: DashboardService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  ngOnDestroy(): void {
    this.destroyCharts();
  }

  loadDashboard(): void {
    this.loading = true;
    this.dashboardService.getAdminDashboard().subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.loading = false;

        this.cdr.detectChanges();

        setTimeout(() => {
          this.initCharts();
        }, 100);
      },
      error: (err) => {
        console.error('Error al cargar el dashboard:', err);
        this.loading = false;
        this.cdr.detectChanges();
      },
    });
  }

  getPercent(value: number): number {
    if (!this.dashboardData) return 0;
    const total =
      this.dashboardData.pendingReservations +
      this.dashboardData.confirmedReservations +
      this.dashboardData.completedReservations +
      this.dashboardData.cancelledReservations +
      this.dashboardData.rejectdReservations;
    return total === 0 ? 0 : Math.round((value / total) * 100);
  }

  private initCharts(): void {
    if (!this.dashboardData) return;

    const lineCanvas = document.getElementById(
      'lineChart',
    ) as HTMLCanvasElement | null;
    const donutCanvas = document.getElementById(
      'donutChart',
    ) as HTMLCanvasElement | null;

    if (!lineCanvas || !donutCanvas) {
      console.warn('Canvas no encontrado, reintentando...');
      setTimeout(() => this.initCharts(), 200);
      return;
    }

    this.destroyCharts();
    this.initLineChart(lineCanvas);
    this.initDonutChart(donutCanvas);
  }

  private destroyCharts(): void {
    this.lineChart?.destroy();
    this.donutChart?.destroy();
    this.lineChart = null;
    this.donutChart = null;
  }

  private initLineChart(canvas: HTMLCanvasElement): void {
    if (!this.dashboardData) return;

    const labels = this.dashboardData.reservationsLastDays.map(([date]) => {
      const d = new Date(date);
      return d.toLocaleDateString('es-PE', { day: '2-digit', month: 'short' });
    });
    const values = this.dashboardData.reservationsLastDays.map(
      ([, count]) => count,
    );

    const isDark = this.isDarkMode;
    const gridColor = isDark ? 'rgba(255,255,255,0.06)' : 'rgba(0,0,0,0.06)';
    const tickColor = isDark ? '#94a3b8' : '#64748b';
    const tooltipBg = isDark ? '#1e293b' : '#ffffff';
    const tooltipText = isDark ? '#e2e8f0' : '#1e293b';
    const accentColor = '#ff6600';

    this.lineChart = new Chart(canvas, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: 'Reservas',
            data: values,
            borderColor: accentColor,
            backgroundColor: isDark
              ? 'rgba(255, 102, 0, 0.08)'
              : 'rgba(255, 102, 0, 0.06)',
            borderWidth: 2.5,
            pointBackgroundColor: accentColor,
            pointBorderColor: isDark ? '#1e293b' : '#ffffff',
            pointBorderWidth: 2,
            pointRadius: 5,
            pointHoverRadius: 7,
            tension: 0.4,
            fill: true,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: tooltipBg,
            titleColor: tooltipText,
            bodyColor: tooltipText,
            borderColor: accentColor,
            borderWidth: 1,
            padding: 12,
            callbacks: {
              label: (ctx) => ` ${ctx.parsed.y} reservas`,
            },
          },
        },
        scales: {
          x: {
            grid: { color: gridColor },
            ticks: { color: tickColor, font: { size: 12 } },
            border: { display: false },
          },
          y: {
            beginAtZero: true,
            grid: { color: gridColor },
            ticks: {
              color: tickColor,
              font: { size: 12 },
              stepSize: 1,
              callback: (val) => (Number.isInteger(Number(val)) ? val : null),
            },
            border: { display: false },
          },
        },
      },
    });
  }

  private initDonutChart(canvas: HTMLCanvasElement): void {
    if (!this.dashboardData) return;

    const isDark = this.isDarkMode;
    const tooltipBg = isDark ? '#1e293b' : '#ffffff';
    const tooltipText = isDark ? '#e2e8f0' : '#1e293b';
    const data = this.dashboardData;

    this.donutChart = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: [
          'Pendientes',
          'Confirmadas',
          'Completadas',
          'Canceladas',
          'Rechazadas',
        ],
        datasets: [
          {
            data: [
              data.pendingReservations,
              data.confirmedReservations,
              data.completedReservations,
              data.cancelledReservations,
              data.rejectdReservations,
            ],
            backgroundColor: [
              'rgba(245, 158, 11, 0.85)',
              'rgba(99, 102, 241, 0.85)',
              'rgba(16, 185, 129, 0.85)',
              'rgba(239, 68, 68, 0.85)',
              'rgba(168, 85, 247, 0.85)',
            ],
            borderColor: isDark ? '#1e293b' : '#ffffff',
            borderWidth: 3,
            hoverOffset: 8,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '68%',
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: tooltipBg,
            titleColor: tooltipText,
            bodyColor: tooltipText,
            borderColor: 'rgba(255,102,0,0.3)',
            borderWidth: 1,
            padding: 12,
            callbacks: {
              label: (ctx) => ` ${ctx.label}: ${ctx.parsed}`,
            },
          },
        },
      },
    });
  }
}
