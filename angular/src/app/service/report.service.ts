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
export class ReportService {
  private apiUrl = `${environment.apiUrl}/reports`;
  constructor(private http: HttpClient) {}

  downloadReservationPdf(
    idReservation: number,
    idUser: string,
  ): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/reservation/${idReservation}/user/${idUser}`, {
      responseType: 'blob',
    });
  }
}