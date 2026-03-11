import { Component, OnInit } from '@angular/core';
import {
  Reservation,
  ReservationStatus,
} from '../../shared/model/reservation.model';
import { ReservationService } from '../../service/reservation.service';
import { AuthService } from '../../service/auth.service';
import { AlertService } from '../../shared/util/sweet-alert';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UpdateReservationStatusRequest } from '../../shared/dto/updatereservation';

@Component({
  selector: 'app-reservation-today',
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation-today.component.html',
  styleUrl: './reservation-today.component.css',
})
export class ReservationTodayComponent implements OnInit {
  reservations: Reservation[] = [];
  filteredReservations: Reservation[] = [];
  searchTerm: string = '';
  loading: boolean = false;
  completingReservation: { [key: number]: boolean } = {};
  today = new Date();
  showCompleteModal: boolean = false;
  completingSelected: Reservation | null = null;
  updateRequest: UpdateReservationStatusRequest = {
    idReservation: 0,
    idRecepcionista: '',
  };

  markingNoShow: { [key: number]: boolean } = {};
  showNoShowModal: boolean = false;
  noShowSelected: Reservation | null = null;
  showViewModal: boolean = false;
  viewReservation: Reservation | null = null;

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.loadTodayReservations();
  }

  loadTodayReservations(): void {
    this.loading = true;
    this.reservationService.getConfirmToday().subscribe({
      next: (data) => {
        this.reservations = data ?? [];
        this.filteredReservations = data ?? [];
        this.loading = false;
      },
      error: () => {
        AlertService.error('Error al cargar reservas de hoy');
        this.loading = false;
      },
    });
  }

  searchReservations(): void {
    const term = this.searchTerm.toLowerCase().trim();
    if (!term) {
      this.filteredReservations = this.reservations;
      return;
    }
    this.filteredReservations = this.reservations.filter(
      (r) =>
        r.customerName.toLowerCase().includes(term) ||
        r.customerEmail.toLowerCase().includes(term) ||
        r.customerPhone.includes(term),
    );
  }

  openCompleteModal(reservation: Reservation): void {
    this.completingSelected = reservation;
    const currentUser = this.authService.getCurrentUserValue();
    this.updateRequest = {
      idReservation: reservation.idReservation || 0,
      idRecepcionista: currentUser?.idUser || '',
      status: 'COMPLETADA',
    };
    this.showCompleteModal = true;
  }

  closeCompleteModal(): void {
    this.showCompleteModal = false;
    this.completingSelected = null;
    this.updateRequest = { idReservation: 0, idRecepcionista: '' };
  }

  completeReservation(): void {
    this.completingReservation[this.updateRequest.idReservation] = true;
    this.reservationService.updateReservation(this.updateRequest).subscribe({
      next: (response) => {
        AlertService.success(
          response.message || 'Reserva completada exitosamente',
        );
        this.completingReservation[this.updateRequest.idReservation] = false;
        this.closeCompleteModal();
        this.loadTodayReservations();
      },
      error: (err) => {
        AlertService.error(
          err.error?.message || 'Error al completar la reserva',
        );
        this.completingReservation[this.updateRequest.idReservation] = false;
      },
    });
  }

  openNoShowModal(reservation: Reservation): void {
    this.noShowSelected = reservation;
    const currentUser = this.authService.getCurrentUserValue();
    this.updateRequest = {
      idReservation: reservation.idReservation || 0,
      idRecepcionista: currentUser?.idUser || '',
      status: 'NO_ASISTIO',
    };
    this.showNoShowModal = true;
  }

  closeNoShowModal(): void {
    this.showNoShowModal = false;
    this.noShowSelected = null;
    this.updateRequest = { idReservation: 0, idRecepcionista: '' };
  }

  markNoShow(): void {
    this.markingNoShow[this.updateRequest.idReservation] = true;
    this.reservationService.updateReservation(this.updateRequest).subscribe({
      next: (response) => {
        AlertService.success(
          response.message || 'Reserva marcada como No Asistió',
        );
        this.markingNoShow[this.updateRequest.idReservation] = false;
        this.closeNoShowModal();
        this.loadTodayReservations();
      },
      error: (err) => {
        AlertService.error(
          err.error?.message || 'Error al actualizar la reserva',
        );
        this.markingNoShow[this.updateRequest.idReservation] = false;
      },
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
      NO_ASISTIO: 'status-noshow',
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
      NO_ASISTIO: 'No Asistió',
    };
    return map[status] || status;
  }

  formatDate(dateString: string): string {
    const [year, month, day] = dateString.split('-');
    return `${day}/${month}/${year}`;
  }

  formatTime(timeString: string): string {
    return timeString?.substring(0, 5) ?? '';
  }

  get totalToday(): number {
    return this.reservations?.length ?? 0;
  }
  get completedToday(): number {
    return this.reservations.filter((r) => r.status === 'COMPLETADA').length;
  }

  get pendingToday(): number {
    return this.reservations.filter((r) => r.status === 'CONFIRMADA').length;
  }
}
