import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { TableCoffe } from '../shared/model/table.model';

@Injectable({
  providedIn: 'root',
})
export class TableCoffeService {
  
  private apiUrl = `${environment.apiUrl}/tablecoffe`;
  private tablesSubject = new BehaviorSubject<TableCoffe[]>([]);
  public tables$ = this.tablesSubject.asObservable();

  constructor(private http: HttpClient) {}

  getAvailableTables(date: string, time: string): Observable<TableCoffe[]> {
    const params = new HttpParams()
      .set('date', date)
      .set('time', time);

    return this.http.get<TableCoffe[]>(`${this.apiUrl}/tables/available`, { params });
  }

  loadAvailableTables(date: string, time: string) {
    this.getAvailableTables(date, time).subscribe({
      next: (tables) => this.tablesSubject.next(tables),
      error: (err) => console.error('Error cargando mesas disponibles', err)
    });
  }
}
