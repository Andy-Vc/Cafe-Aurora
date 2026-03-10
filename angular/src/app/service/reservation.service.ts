import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Reservation } from '../shared/model/reservation.model';
import { ResultResponse } from '../shared/dto/resultresponse';
import { ConfirmReservationRequest } from '../shared/dto/confirmreservation';
import { RejectdReservation } from '../shared/dto/rejectreservation';
import { CancelReservation } from '../shared/dto/cancelreservation';
import { CompleteReservation } from '../shared/dto/completereservation';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private apiUrl = `${environment.apiUrl}/reservation`;
  constructor(private http: HttpClient) {}

  /*  RECEPCIONIST  */
  getPendientes(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/list/pendientes`);
  }

  confirmReservation(
    request: ConfirmReservationRequest,
  ): Observable<ResultResponse> {
    return this.http.put<ResultResponse>(
      `${this.apiUrl}/confirm/${request.idReservation}`,
      request,
    );
  }

  rejectdReservation(request: RejectdReservation): Observable<ResultResponse> {
    return this.http.put<ResultResponse>(
      `${this.apiUrl}/reject/${request.idReservation}`,
      request,
    );
  }

  completedReservation(request: CompleteReservation): Observable<ResultResponse> {
    return this.http.put<ResultResponse>(
      `${this.apiUrl}/complete/${request.idReservation}`,
      request,
    );
  }

  getConfirmedByRecepcionist(
    idRecepcionista: string,
  ): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(
      `${this.apiUrl}/list/confirmed/${idRecepcionista}`,
    );
  }

  getRejectdByRecepcionist(
    idRecepcionista: string,
  ): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(
      `${this.apiUrl}/list/rejectd/${idRecepcionista}`,
    );
  }

  getConfirmToday(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(
      `${this.apiUrl}/confirmed/today`,
    );
  }

  /*  CUSTOMER  */
  createReservation(reservation: Reservation): Observable<ResultResponse> {
    return this.http.post<ResultResponse>(
      `${this.apiUrl}/register`,
      reservation,
    );
  }

  cancelReservation(request: CancelReservation): Observable<ResultResponse> {
    return this.http.put<ResultResponse>(`${this.apiUrl}/cancel`, request);
  }

  getActiveReservations(idUser: string): Observable<any> {
    return this.http.get<Reservation>(`${this.apiUrl}/list/active/${idUser}`);
  }

  getHistoryReservations(idUser: string): Observable<any> {
    return this.http.get<Reservation>(`${this.apiUrl}/list/history/${idUser}`);
  }

  countReservationsByUser(idUser: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/${idUser}`);
  }
}
