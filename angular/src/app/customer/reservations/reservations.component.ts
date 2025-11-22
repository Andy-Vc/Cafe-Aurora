import { Component, OnInit } from '@angular/core';
import { AlertService } from '../../shared/util/sweet-alert';
import { Reservation } from '../../shared/model/reservation.model';
import { UserResponse } from '../../shared/dto/userresponse';
import { AuthService } from '../../service/auth.service';
import { ReservationService } from '../../service/reservation.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reservations',
  imports: [CommonModule],
  templateUrl: './reservations.component.html',
  styleUrl: './reservations.component.css',
})
export class ReservationsComponent implements OnInit {
  currentUser: UserResponse | null = null;
  reservations: Reservation[] = [];
  loading = true;
  downloadingPdf: { [key: number]: boolean } = {};

  constructor(
    private authService: AuthService,
    private reservationService: ReservationService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe({
      next: (user) => {
        this.currentUser = user;
        if (user?.idUser) {
          this.loadReservations(user.idUser);
        } else {
          this.loading = false;
        }
      },
    });
  }

  loadReservations(userId: string): void {
    this.loading = true;

    this.reservationService.getActiveReservations(userId).subscribe({
      next: (data) => {
        this.reservations = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading reservations:', err);
        AlertService.error(
          'Error al cargar reservas',
          'No se pudieron cargar tus reservas. Por favor intenta nuevamente.'
        );
        this.loading = false;
      },
    });
  }
  downloadPdf(reservation: Reservation): void {
    if (!this.currentUser?.idUser) {
      AlertService.info('Error', 'Debes iniciar sesión para descargar el PDF');
      return;
    }

    this.downloadingPdf[reservation.idReservation!] = true;

    this.reservationService
      .downloadReservationPdf(
        reservation.idReservation!,
        this.currentUser.idUser
      )
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `reserva-${
            reservation.idReservation
          }-${this.formatDateForFilename(reservation.reservationDate)}.pdf`;
          link.click();

          window.URL.revokeObjectURL(url);
          this.downloadingPdf[reservation.idReservation!] = false;

          AlertService.success(
            'PDF descargado',
            'Tu reserva se ha descargado correctamente'
          );
        },
        error: (err) => {
          console.error('Error downloading PDF:', err);
          this.downloadingPdf[reservation.idReservation!] = false;
          AlertService.error(
            'Error al descargar PDF',
            'No se pudo descargar el PDF. Por favor intenta nuevamente.'
          );
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

  formatDateForFilename(date: string): string {
    return new Date(date).toISOString().split('T')[0];
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      PENDING: 'status-pending',
      CONFIRMED: 'status-confirmed',
    };
    return statusMap[status.toUpperCase()] || 'status-pending';
  }

  getStatusLabel(status: string): string {
    const labelMap: { [key: string]: string } = {
      PENDING: 'Pendiente',
      CONFIRMED: 'Confirmada',
    };
    return labelMap[status.toUpperCase()] || status;
  }

  getStatusIcon(status: string): string {
    const iconMap: { [key: string]: string } = {
      PENDING: 'bi-clock-history',
      CONFIRMED: 'bi-check-circle-fill',
    };
    return iconMap[status.toUpperCase()] || 'bi-clock-history';
  }

  isUpcoming(date: string): boolean {
    return new Date(date) > new Date();
  }

  get totalReservations(): number {
    return this.reservations.length;
  }

  get upcomingReservations(): number {
    return this.reservations.filter((r) => this.isUpcoming(r.reservationDate))
      .length;
  }

  get confirmedReservations(): number {
    return this.reservations.filter(
      (r) => r.status.toUpperCase() === 'CONFIRMADA'
    ).length;
  }
}
