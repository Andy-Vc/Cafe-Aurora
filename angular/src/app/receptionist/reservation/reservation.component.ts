import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  Reservation,
  ReservationStatus,
} from '../../shared/model/reservation.model';
import { TableCoffe } from '../../shared/model/table.model';
import { ConfirmReservationRequest } from '../../shared/dto/confirmreservation';
import { ReservationService } from '../../service/reservation.service';
import { TableCoffeService } from '../../service/tablecoffe.service';
import { AuthService } from '../../service/auth.service';
import { AlertService } from '../../shared/util/sweet-alert';
import { RejectdReservation } from '../../shared/dto/rejectreservation';

@Component({
  selector: 'app-reservation',
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation.component.html',
  styleUrl: './reservation.component.css',
})
export class ReservationComponent implements OnInit {
  reservations: Reservation[] = [];
  filteredReservations: Reservation[] = [];
  availableTables: TableCoffe[] = [];
  searchTerm: string = '';
  loading: boolean = false;

  showConfirmModal: boolean = false;
  selectedReservation: Reservation | null = null;
  confirmRequest: ConfirmReservationRequest = {
    idReservation: 0,
    idRecepcionista: '',
    idTable: 0,
    notes: '',
  };

  showRejectModal: boolean = false;
  rejectingReservation: Reservation | null = null;
  rejectRequest: RejectdReservation = {
    idReservation: 0,
    idRecepcionista: '',
    notes: '',
  };

  showViewModal: boolean = false;
  viewReservation: Reservation | null = null;

  activeTab: 'pendientes' | 'confirmadas' | 'rechazadas' = 'pendientes';

  constructor(
    private reservationService: ReservationService,
    private tableService: TableCoffeService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.loadPendingReservations();
  }

  loadPendingReservations(): void {
    this.loading = true;
    this.reservationService.getPendientes().subscribe({
      next: (data) => {
        this.reservations = data;
        this.filteredReservations = data;
        this.loading = false;
      },
      error: (err) => {
        AlertService.error('Error al cargar reservas pendientes');
        this.loading = false;
      },
    });
  }

  loadConfirmedReservations(): void {
    this.loading = true;
    this.reservations = [];
    this.filteredReservations = [];
    const currentUser = this.authService.getCurrentUserValue();
    if (currentUser?.idUser) {
      this.reservationService
        .getConfirmedByRecepcionist(currentUser.idUser)
        .subscribe({
          next: (data) => {
            this.reservations = data ?? [];
            this.filteredReservations = data ?? [];
            this.loading = false;
          },
          error: (err) => {
            AlertService.error('Error al cargar reservas confirmadas');
            this.reservations = [];
            this.filteredReservations = [];
            this.loading = false;
          },
        });
    } else {
      this.loading = false;
    }
  }

  loadRejectdReservations(): void {
    this.loading = true;
    this.reservations = [];
    this.filteredReservations = [];
    const currentUser = this.authService.getCurrentUserValue();
    if (currentUser?.idUser) {
      this.reservationService
        .getRejectdByRecepcionist(currentUser.idUser)
        .subscribe({
          next: (data) => {
            this.reservations = data ?? [];
            this.filteredReservations = data ?? [];
            this.loading = false;
          },
          error: (err) => {
            AlertService.error('Error al cargar reservas rechazadas');
            this.reservations = [];
            this.filteredReservations = [];
            this.loading = false;
          },
        });
    } else {
      this.loading = false;
    }
  }

  switchTab(tab: 'pendientes' | 'confirmadas' | 'rechazadas'): void {
    this.activeTab = tab;
    this.searchTerm = '';
    if (tab === 'pendientes') {
      this.loadPendingReservations();
    } else if (tab === 'confirmadas') {
      this.loadConfirmedReservations();
    } else {
      this.loadRejectdReservations();
    }
  }

  searchReservations(): void {
    const term = this.searchTerm.toLowerCase().trim();
    if (!term) {
      this.filteredReservations = this.reservations;
      return;
    }

    this.filteredReservations = this.reservations.filter(
      (reservation) =>
        reservation.customerName.toLowerCase().includes(term) ||
        reservation.customerPhone.includes(term),
    );
  }

  openRejectModal(reservation: Reservation): void {
    this.rejectingReservation = reservation;
    const currentUser = this.authService.getCurrentUserValue();
    this.rejectRequest = {
      idReservation: reservation.idReservation || 0,
      idRecepcionista: currentUser?.idUser || '',
      notes: '',
    };
    this.showRejectModal = true;
  }

  closeRejectModal(): void {
    this.showRejectModal = false;
    this.rejectingReservation = null;
    this.rejectRequest = { idReservation: 0, idRecepcionista: '', notes: '' };
  }

  rejectReservation(): void {
    this.loading = true;
    this.reservationService.rejectdReservation(this.rejectRequest).subscribe({
      next: (response) => {
        AlertService.success(response.message || 'Reserva rechazada');
        this.closeRejectModal();
        this.loadPendingReservations();
      },
      error: (err) => {
        AlertService.error(
          err.error?.message || 'Error al rechazar la reserva',
        );
        this.loading = false;
      },
    });
  }

  openConfirmModal(reservation: Reservation): void {
    this.selectedReservation = reservation;
    this.showConfirmModal = true;

    // Cargar mesas disponibles para esa fecha y hora
    this.tableService
      .getAvailableTables(
        reservation.reservationDate,
        reservation.reservationTime,
      )
      .subscribe({
        next: (tables) => {
          this.availableTables = tables.filter(
            (t) =>
              t.isAvailable &&
              t.status === 'DISPONIBLE' &&
              t.capacity >= reservation.numPeople,
          );
        },
        error: (err) => {
          AlertService.error('Error al cargar mesas disponibles');
          this.availableTables = [];
        },
      });

    // Preparar request
    const currentUser = this.authService.getCurrentUserValue();
    this.confirmRequest = {
      idReservation: reservation.idReservation || 0,
      idRecepcionista: currentUser?.idUser || '',
      idTable: 0,
      notes: '',
    };
  }

  closeConfirmModal(): void {
    this.showConfirmModal = false;
    this.selectedReservation = null;
    this.availableTables = [];
    this.confirmRequest = {
      idReservation: 0,
      idRecepcionista: '',
      idTable: 0,
      notes: '',
    };
  }

  confirmReservation(): void {
    if (!this.confirmRequest.idTable) {
      AlertService.info('Por favor selecciona una mesa');
      return;
    }

    this.loading = true;
    console.log('Esto es lo que manda: ' + JSON.stringify(this.confirmRequest));
    this.reservationService.confirmReservation(this.confirmRequest).subscribe({
      next: (response) => {
        AlertService.success(
          response.message || 'Reserva confirmada exitosamente',
        );
        this.closeConfirmModal();
        this.loadPendingReservations();
      },
      error: (err) => {
        AlertService.error(
          err.error?.message || 'Error al confirmar la reserva',
        );
        this.loading = false;
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
    const statusClasses = {
      PENDIENTE: 'status-pending',
      CONFIRMADA: 'status-confirmed',
      CANCELADA: 'status-cancelled',
      COMPLETADA: 'status-completed',
      RECHAZADA: 'status-cancelled',
    };
    return statusClasses[status] || '';
  }

  getStatusText(status: ReservationStatus): string {
    const statusTexts = {
      PENDIENTE: 'Pendiente',
      CONFIRMADA: 'Confirmada',
      CANCELADA: 'Cancelada',
      COMPLETADA: 'Completada',
      RECHAZADA: 'Rechazada',
    };
    return statusTexts[status] || status;
  }

  formatDate(dateString: string): string {
    const [year, month, day] = dateString.split('-');
    return `${day}/${month}/${year}`;
  }

  formatTime(timeString: string): string {
    return timeString.substring(0, 5);
  }
}
