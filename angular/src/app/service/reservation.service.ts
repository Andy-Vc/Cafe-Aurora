import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
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

  /*  RECEPCIONIST  */
  getPendientes(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/list/pendientes`);
  }

  confirmReservation(
    idReservation: number,
    idRecepcionista: string,
    idTable: number,
    notes?: string
  ): Observable<ResultResponse> {
    let params = new HttpParams()
      .set('idRecepcionista', idRecepcionista)
      .set('idTable', idTable.toString());

    if (notes) {
      params = params.set('notes', notes);
    }

    return this.http.put<ResultResponse>(
      `${this.apiUrl}/confirm/${idReservation}`,
      null,
      { params }
    );
  }

  getConfirmedByRecepcionist(
    idRecepcionista: string
  ): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(
      `${this.apiUrl}/list/confirmed/${idRecepcionista}`
    );
  }

  /*  CUSTOMER  */
  createReservation(reservation: Reservation): Observable<ResultResponse> {
    return this.http.post<ResultResponse>(
      `${this.apiUrl}/register`,
      reservation
    );
  }

  getActiveReservations(idUser: string): Observable<any> {
    return this.http.get<Reservation>(`${this.apiUrl}/list/active/${idUser}`);
  }

  getHistoryReservations(idUser: string): Observable<any> {
    return this.http.get<Reservation>(`${this.apiUrl}/list/history/${idUser}`);
  }

  downloadReservationPdf(
    idReservation: number,
    idUser: string
  ): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/pdf/${idReservation}/user/${idUser}`, {
      responseType: 'blob',
    });
  }

  countReservationsByUser(idUser: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/${idUser}`);
  }
}
