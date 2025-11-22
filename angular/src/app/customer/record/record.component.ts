import { Component, OnInit } from '@angular/core';
import { UserResponse } from '../../shared/dto/userresponse';
import { Reservation } from '../../shared/model/reservation.model';
import { AuthService } from '../../service/auth.service';
import { ReservationService } from '../../service/reservation.service';
import { CommonModule } from '@angular/common';
import { AlertService } from '../../shared/util/sweet-alert';

@Component({
  selector: 'app-record',
  imports: [CommonModule],
  templateUrl: './record.component.html',
  styleUrl: './record.component.css',
})
export class RecordComponent implements OnInit {
  currentUser: UserResponse | null = null;
  reservations: Reservation[] = [];
  loading = true;

  constructor(
    private authService: AuthService,
    private reservationService: ReservationService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe({
      next: (user) => {
        this.currentUser = user;
        if (user?.idUser) {
          this.loadHistoryReservations(user.idUser);
        } else {
          this.loading = false;
        }
      },
    });
  }

  loadHistoryReservations(userId: string): void {
    this.loading = true;

    this.reservationService.getHistoryReservations(userId).subscribe({
      next: (data) => {
        this.reservations = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading history reservations:', err);
        AlertService.error(
          'Error al cargar historial',
          'No se pudo cargar tu historial de reservas. Por favor intenta nuevamente.'
        );
        this.loading = false;
      },
    });
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('es-ES', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  formatTime(date: string): string {
    return new Date(date).toLocaleTimeString('es-ES', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      CANCELLED: 'status-cancelled',
      COMPLETED: 'status-completed',
    };
    return statusMap[status.toUpperCase()] || 'status-pending';
  }

  getStatusLabel(status: string): string {
    const labelMap: { [key: string]: string } = {
      CANCELLED: 'Cancelada',
      COMPLETED: 'Completada',
    };
    return labelMap[status.toUpperCase()] || status;
  }

  getStatusIcon(status: string): string {
    const iconMap: { [key: string]: string } = {
      CANCELLED: 'bi-x-circle-fill',
      COMPLETED: 'bi-check-all',
    };
    return iconMap[status.toUpperCase()] || 'bi-clock-history';
  }

  get totalReservations(): number {
    return this.reservations?.length ?? 0;
  }

  get completedReservations(): number {
    return (
      this.reservations?.filter((r) => r.status?.toUpperCase() === 'COMPLETED')
        ?.length ?? 0
    );
  }

  get cancelledReservations(): number {
    return (
      this.reservations?.filter((r) => r.status?.toUpperCase() === 'CANCELLED')
        ?.length ?? 0
    );
  }
}
