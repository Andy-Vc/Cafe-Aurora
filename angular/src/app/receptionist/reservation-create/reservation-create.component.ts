import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AlertService } from '../../shared/util/sweet-alert';
import { Reservation } from '../../shared/model/reservation.model';
import { ReservationService } from '../../service/reservation.service';
import { TableCoffeService } from '../../service/tablecoffe.service';
import { AuthService } from '../../service/auth.service';
import { TableCoffe } from '../../shared/model/table.model';

@Component({
  selector: 'app-reservation-create',
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation-create.component.html',
  styleUrl: './reservation-create.component.css',
})
export class ReservationCreateComponent implements OnInit {
  loading = false;
  loadingTables = false;
  availableTables: TableCoffe[] = [];
  tablesLoaded = false;

  reservation: Reservation = this.getEmptyReservation();

  minDate: string = '';

  constructor(
    private reservationService: ReservationService,
    private tableService: TableCoffeService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.minDate = new Date().toISOString().split('T')[0];
  }

  getEmptyReservation(): Reservation {
    return {
      reservationDate: '',
      reservationTime: '',
      numPeople: 1,
      customerName: '',
      customerPhone: '',
      customerEmail: '',
      specialNotes: '',
      table: { idTable: 0 } as any,
      attendedBy: { idUser: '' } as any,
    } as Reservation;
  }

  onDateOrTimeChange(): void {
    if (this.reservation.reservationDate && this.reservation.reservationTime) {
      this.loadAvailableTables();
    }
    this.reservation.table = { idTable: 0 } as any;
    this.tablesLoaded = false;
  }

  loadAvailableTables(): void {
    this.loadingTables = true;
    this.tablesLoaded = false;
    this.tableService
      .getAvailableTables(
        this.reservation.reservationDate,
        this.reservation.reservationTime,
      )
      .subscribe({
        next: (tables) => {
          this.availableTables = tables.filter(
            (t) => t.isAvailable && t.capacity >= this.reservation.numPeople,
          );
          this.loadingTables = false;
          this.tablesLoaded = true;
        },
        error: () => {
          AlertService.error('Error al cargar mesas disponibles');
          this.loadingTables = false;
          this.tablesLoaded = true;
        },
      });
  }

  onNumPeopleChange(): void {
    if (this.reservation.reservationDate && this.reservation.reservationTime) {
      this.reservation.table = { idTable: 0 } as any;
      this.loadAvailableTables();
    }
  }

  isFormValid(): boolean {
    return !!(
      this.reservation.customerName.trim() &&
      this.reservation.reservationDate &&
      this.reservation.reservationTime &&
      this.reservation.numPeople >= 1 &&
      this.reservation.table?.idTable
    );
  }

  submit(): void {
    if (!this.isFormValid()) {
      AlertService.info('Completa todos los campos requeridos');
      return;
    }

    const currentUser = this.authService.getCurrentUserValue();
    if (!currentUser?.idUser) {
      AlertService.error('Error', 'No se pudo obtener el usuario actual');
      return;
    }

    this.reservation.attendedBy = { idUser: currentUser.idUser } as any;
    this.loading = true;

    this.reservationService
      .createReservationReceptionist(this.reservation)
      .subscribe({
        next: (response) => {
          AlertService.success(
            response.message || 'Reserva creada correctamente',
          );
          this.reservation = this.getEmptyReservation();
          this.availableTables = [];
          this.tablesLoaded = false;
          this.loading = false;
        },
        error: (err) => {
          AlertService.error(err.error?.message || 'Error al crear la reserva');
          this.loading = false;
        },
      });
  }

  reset(): void {
    this.reservation = this.getEmptyReservation();
    this.availableTables = [];
    this.tablesLoaded = false;
  }
}
