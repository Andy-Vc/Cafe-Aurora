import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../service/dashboard.service';
import { DashboardRecepcionist } from '../../shared/dto/dashboardrecepcionist';
import { AlertService } from '../../shared/util/sweet-alert';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  dashboard: DashboardRecepcionist | null = null;
  loading = true;
  today = new Date();

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    this.dashboardService.getRecepcionistDashboard().subscribe({
      next: (data) => {
        this.dashboard = data;
        console.log(data);
        this.loading = false;
      },
      error: () => {
        AlertService.error('Error al cargar el dashboard');
        this.loading = false;
      },
    });
  }

  formatTime(timeString: string): string {
    return timeString?.substring(0, 5) ?? '';
  }

  formatDate(dateString: string): string {
    const [year, month, day] = dateString.split('-');
    return `${day}/${month}/${year}`;
  }

  getStatusClass(status: string): string {
    const map: any = {
      PENDIENTE: 'status-pending',
      CONFIRMADA: 'status-confirmed',
      CANCELADA: 'status-cancelled',
      COMPLETADA: 'status-completed',
      RECHAZADA: 'status-cancelled',
    };
    return map[status] || '';
  }

  getStatusText(status: string): string {
    const map: any = {
      PENDIENTE: 'Pendiente',
      CONFIRMADA: 'Confirmada',
      CANCELADA: 'Cancelada',
      COMPLETADA: 'Completada',
      RECHAZADA: 'Rechazada',
    };
    return map[status] || status;
  }

  getGreeting(): string {
    const hour = new Date().getHours();
    if (hour < 12) return 'Buenos días';
    if (hour < 18) return 'Buenas tardes';
    return 'Buenas noches';
  }
}
