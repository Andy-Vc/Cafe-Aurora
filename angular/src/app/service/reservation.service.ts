import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Reservation } from '../shared/model/reservation.model';
import { ResultResponse } from '../shared/dto/resultresponse';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private apiUrl = `${environment.apiUrl}/reservation`;
  constructor(private http: HttpClient) {}

  createReservation(reservation: Reservation): Observable<ResultResponse> {
    return this.http.post<ResultResponse>(`${this.apiUrl}/register`, reservation);
  }
}
