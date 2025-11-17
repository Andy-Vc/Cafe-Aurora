import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { ReservationService } from '../../service/reservation.service';
import { UserResponse } from '../../shared/dto/userresponse';
import { Reservation } from '../../shared/model/reservation.model';
import { AlertService } from '../../shared/util/sweet-alert';

interface ReservationForm {
  fullName: string;
  email: string;
  phone: string;
  date: string;
  time: string;
  numberOfPeople: number;
  message: string;
}

@Component({
  selector: 'app-reservation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation.component.html',
  styleUrl: './reservation.component.css',
})
export class ReservationComponent implements OnInit {
  currentUser: UserResponse | null = null;
  loading = false;

  reservationForm: ReservationForm = {
    fullName: '',
    email: '',
    phone: '',
    date: '',
    time: '',
    numberOfPeople: 2,
    message: '',
  };

  contactInfo = {
    address: 'Calle 85 #15-32, Chapinero, Bogotá',
    phone: '+57 1 234-5678',
    email: 'hola@cafeaurora.co',
    schedule: {
      weekdays: 'Lun-Vie: 6:00 AM - 10:00 PM',
      weekend: 'Sáb-Dom: 7:00 AM - 11:00 PM',
    },
    social: {
      instagram: '@cafeaurora',
      facebook: 'Facebook',
    },
  };

  constructor(
    private authService: AuthService,
    private reservationService: ReservationService
  ) {}

  ngOnInit(): void {
    // Pre-fill form with user data if logged in
    this.authService.currentUser$.subscribe({
      next: (user) => {
        this.currentUser = user;
        if (user) {
          this.reservationForm.fullName = user.name || '';
          this.reservationForm.email = user.email || '';
          this.reservationForm.phone = user.phone || '';
        }
      },
    });
  }

  onSubmit(): void {
    if (!this.currentUser) {
      AlertService.info('Necesitas iniciar sesión para hacer una reserva');
      return;
    }

    if (this.validateForm()) {
      this.loading = true;

      const reservation: Reservation = {
        customerName: this.reservationForm.fullName,
        customerEmail: this.reservationForm.email,
        customerPhone: this.reservationForm.phone,
        reservationDate: this.reservationForm.date,
        reservationTime: this.reservationForm.time,
        numPeople: this.reservationForm.numberOfPeople,
        specialNotes: this.reservationForm.message || undefined,
        user: { idUser: this.currentUser!.idUser },
        status: 'PENDIENTE',
        createdAt: '',
        updatedAt: '',
      };

      // Call API
      this.reservationService.createReservation(reservation).subscribe({
        next: (response) => {
          this.loading = false;
          if (response.value) {
            AlertService.success(response.message);
            this.resetForm();
          } else {
            AlertService.error(
              response.message || 'Por favor intenta nuevamente.'
            );
          }
        },
        error: (error) => {
          this.loading = false;
          console.error('Error creating reservation:', error);
          AlertService.error(
            error ||
              'Ocurrió un problema al procesar tu solicitud. Por favor intenta nuevamente.'
          );
        },
      });
    }
  }

  combineDateTime(date: string, time: string): string {
    return `${date}T${time}:00`;
  }

  validateForm(): boolean {
    const { fullName, email, phone, date, time, numberOfPeople } =
      this.reservationForm;

    if (!fullName.trim()) {
      AlertService.info(
        'Campo requerido',
        'Por favor ingresa tu nombre completo'
      );
      return false;
    }

    if (!email.trim() || !this.isValidEmail(email)) {
      AlertService.info('Email inválido', 'Por favor ingresa un email válido');
      return false;
    }

    if (!phone.trim()) {
      AlertService.info('Campo requerido', 'Por favor ingresa tu teléfono');
      return false;
    }

    if (!date) {
      AlertService.info('Campo requerido', 'Por favor selecciona una fecha');
      return false;
    }

    if (!time) {
      AlertService.info('Campo requerido', 'Por favor selecciona una hora');
      return false;
    }

    if (numberOfPeople < 1 || numberOfPeople > 20) {
      AlertService.info(
        'Número inválido',
        'El número de personas debe estar entre 1 y 20'
      );
      return false;
    }

    const selectedDate = new Date(`${date}T${time}`);
    const now = new Date();

    if (selectedDate < now) {
      AlertService.info(
        'Fecha inválida',
        'No puedes hacer una reserva en el pasado'
      );
      return false;
    }

    return true;
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  resetForm(): void {
    if (this.currentUser) {
      this.reservationForm = {
        fullName: this.currentUser.name || '',
        email: this.currentUser.email || '',
        phone: this.currentUser.phone || '',
        date: '',
        time: '',
        numberOfPeople: 2,
        message: '',
      };
    } else {
      this.reservationForm = {
        fullName: '',
        email: '',
        phone: '',
        date: '',
        time: '',
        numberOfPeople: 2,
        message: '',
      };
    }
  }

  incrementPeople(): void {
    if (this.reservationForm.numberOfPeople < 20) {
      this.reservationForm.numberOfPeople++;
    }
  }

  decrementPeople(): void {
    if (this.reservationForm.numberOfPeople > 1) {
      this.reservationForm.numberOfPeople--;
    }
  }

  openGoogleMaps(): void {
    window.open(
      'https://www.google.com/maps/search/?api=1&query=Calle+85+15-32+Chapinero+Bogota',
      '_blank'
    );
  }

  openInstagram(): void {
    window.open('https://www.instagram.com/cafeaurora', '_blank');
  }

  openFacebook(): void {
    window.open('https://www.facebook.com/cafeaurora', '_blank');
  }

  getCurrentDate(): string {
    return new Date().toISOString().split('T')[0];
  }
}
