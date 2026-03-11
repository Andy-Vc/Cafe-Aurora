import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ReportFilters } from '../shared/dto/reportfilters';
import { ReportReceptionistDTO } from '../shared/dto/reportreceptionist';
import { ReportTableDTO } from '../shared/dto/reporttable';
import { ReportReservationDTO } from '../shared/dto/reportreservation';

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

  getReservationsReport(filters: ReportFilters): Observable<ReportReservationDTO[]> {
    return this.http.get<ReportReservationDTO[]>(
      `${this.apiUrl}/reservations`,
      { params: this.buildParams(filters) }
    );
  }

  downloadReservationsPdf(filters: ReportFilters): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/reservations/pdf`, {
      params: this.buildParams(filters),
      responseType: 'blob',
    });
  }

  downloadReservationsExcel(filters: ReportFilters): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/reservations/excel`, {
      params: this.buildParams(filters),
      responseType: 'blob',
    });
  }

  getTablesReport(start: string, end: string): Observable<ReportTableDTO[]> {
    return this.http.get<ReportTableDTO[]>(
      `${this.apiUrl}/tables`,
      { params: new HttpParams().set('start', start).set('end', end) }
    );
  }

  downloadTablesPdf(start: string, end: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/tables/pdf`, {
      params: new HttpParams().set('start', start).set('end', end),
      responseType: 'blob',
    });
  }

  downloadTablesExcel(start: string, end: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/tables/excel`, {
      params: new HttpParams().set('start', start).set('end', end),
      responseType: 'blob',
    });
  }

  getReceptionistsReport(start: string, end: string): Observable<ReportReceptionistDTO[]> {
    return this.http.get<ReportReceptionistDTO[]>(
      `${this.apiUrl}/receptionists`,
      { params: new HttpParams().set('start', start).set('end', end) }
    );
  }

  downloadReceptionistsPdf(start: string, end: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/receptionists/pdf`, {
      params: new HttpParams().set('start', start).set('end', end),
      responseType: 'blob',
    });
  }

  downloadReceptionistsExcel(start: string, end: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/receptionists/excel`, {
      params: new HttpParams().set('start', start).set('end', end),
      responseType: 'blob',
    });
  }

  saveFile(blob: Blob, filename: string): void {
    const url  = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href     = url;
    link.download = filename;
    link.click();
    URL.revokeObjectURL(url);
  }

  private buildParams(filters: ReportFilters): HttpParams {
    let params = new HttpParams()
      .set('start', filters.start)
      .set('end',   filters.end);

    if (filters.status) params = params.set('status', filters.status);
    if (filters.source) params = params.set('source', filters.source);

    return params;
  }
}