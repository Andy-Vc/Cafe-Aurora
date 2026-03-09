import { Component, OnInit } from '@angular/core';
import { Reservation, ReservationStatus } from '../../shared/model/reservation.model';
import { CompleteReservation } from '../../shared/dto/completereservation';
import { ReservationService } from '../../service/reservation.service';
import { AuthService } from '../../service/auth.service';
import { AlertService } from '../../shared/util/sweet-alert';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reservation-today',
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation-today.component.html',
  styleUrl: './reservation-today.component.css'
})
export class ReservationTodayComponent implements OnInit {
  reservations: Reservation[] = [];
  filteredReservations: Reservation[] = [];
  searchTerm: string = '';
  loading: boolean = false;
  completingReservation: { [key: number]: boolean } = {};
  today = new Date();
  // Modal completar
  showCompleteModal: boolean = false;
  completingSelected: Reservation | null = null;
  completeRequest: CompleteReservation = {
    idReservation: 0,
    idRecepcionista: ''
  };

  // Modal ver detalle
  showViewModal: boolean = false;
  viewReservation: Reservation | null = null;

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadTodayReservations();
  }

  loadTodayReservations(): void {
    this.loading = true;
    this.reservationService.getConfirmToday().subscribe({
      next: (data) => {
        this.reservations = data;
        this.filteredReservations = data;
        this.loading = false;
      },
      error: () => {
        AlertService.error('Error al cargar reservas de hoy');
        this.loading = false;
      }
    });
  }

  searchReservations(): void {
    const term = this.searchTerm.toLowerCase().trim();
    if (!term) {
      this.filteredReservations = this.reservations;
      return;
    }
    this.filteredReservations = this.reservations.filter(r =>
      r.customerName.toLowerCase().includes(term) ||
      r.customerEmail.toLowerCase().includes(term) ||
      r.customerPhone.includes(term)
    );
  }

  openCompleteModal(reservation: Reservation): void {
    this.completingSelected = reservation;
    const currentUser = this.authService.getCurrentUserValue();
this.completeRequest = {
  idReservation: reservation.idReservation || 0,
  idRecepcionista: currentUser?.idUser || ''
};
    this.showCompleteModal = true;
  }

  closeCompleteModal(): void {
    this.showCompleteModal = false;
    this.completingSelected = null;
    this.completeRequest = { idReservation: 0, idRecepcionista: '' };
  }

  completeReservation(): void {
    this.completingReservation[this.completeRequest.idReservation] = true;
    this.reservationService.completedReservation(this.completeRequest).subscribe({
      next: (response) => {
        AlertService.success(response.message || 'Reserva completada exitosamente');
        this.completingReservation[this.completeRequest.idReservation] = false;
        this.closeCompleteModal();
        this.loadTodayReservations();
      },
      error: (err) => {
        AlertService.error(err.error?.message || 'Error al completar la reserva');
        this.completingReservation[this.completeRequest.idReservation] = false;
      }
    });
  }

  openViewModal(reservation: Reservation): void {
    this.viewReservation = reservation;
    this.showViewModal = true;
  }

  closeViewModal(): void {
    this.showViewModal = false;
    this.viewReservation = null;
  }

  getStatusClass(status: ReservationStatus): string {
    const map: any = {
      PENDIENTE: 'status-pending',
      CONFIRMADA: 'status-confirmed',
      CANCELADA: 'status-cancelled',
      COMPLETADA: 'status-completed',
      RECHAZADA: 'status-cancelled',
    };
    return map[status] || '';
  }

  getStatusText(status: ReservationStatus): string {
    const map: any = {
      PENDIENTE: 'Pendiente',
      CONFIRMADA: 'Confirmada',
      CANCELADA: 'Cancelada',
      COMPLETADA: 'Completada',
      RECHAZADA: 'Rechazada',
    };
    return map[status] || status;
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('es-PE', {
      day: '2-digit', month: '2-digit', year: 'numeric'
    });
  }

  formatTime(timeString: string): string {
    return timeString?.substring(0, 5) ?? '';
  }

  get totalToday(): number { return this.reservations.length; }
  get completedToday(): number {
    return this.reservations.filter(r => r.status === 'COMPLETADA' as ReservationStatus).length;
  }
  get pendingToday(): number {
    return this.reservations.filter(r => r.status === 'CONFIRMADA' as ReservationStatus).length;
  }
}
