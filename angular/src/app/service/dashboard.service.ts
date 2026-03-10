import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DashboardRecepcionist } from '../shared/dto/dashboardrecepcionist';
import { DashboardAdmin } from '../shared/dto/dashboardadmin';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {

  private apiUrl = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  getRecepcionistDashboard(): Observable<DashboardRecepcionist> {
    return this.http.get<DashboardRecepcionist>(`${this.apiUrl}/receptionist`);
  }

  getAdminDashboard(): Observable<DashboardAdmin> {
    return this.http.get<DashboardAdmin>(`${this.apiUrl}/admin`);
  }
}