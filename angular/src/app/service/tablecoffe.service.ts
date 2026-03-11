import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { TableCoffe } from '../shared/model/table.model';
import { ResultResponse } from '../shared/dto/resultresponse';

@Injectable({
  providedIn: 'root',
})
export class TableCoffeService {
  private apiUrl = `${environment.apiUrl}/tablecoffe`;
  private tablesSubject = new BehaviorSubject<TableCoffe[]>([]);
  public tables$ = this.tablesSubject.asObservable();

  constructor(private http: HttpClient) {}

  getAvailableTables(date: string, time: string): Observable<TableCoffe[]> {
    const params = new HttpParams().set('date', date).set('time', time);

    return this.http.get<TableCoffe[]>(`${this.apiUrl}/tables/available`, {
      params,
    });
  }

  /*Crud Tables Services */
  allTables(): void {
    this.http.get<TableCoffe[]>(`${this.apiUrl}/list`).subscribe({
      next: (data) => this.tablesSubject.next(data),
      error: (err) => console.error('Error fetching mesas', err),
    });
  }

  createTable(table: TableCoffe): Observable<ResultResponse> {
    return this.http.post<ResultResponse>(`${this.apiUrl}/register`, table);
  }

  getTableById(id: number): Observable<TableCoffe> {
    return this.http.get<TableCoffe>(`${this.apiUrl}/id/${id}`);
  }

  updateTable(table: TableCoffe): Observable<ResultResponse> {
    return this.http.patch<ResultResponse>(`${this.apiUrl}/update`, table);
  }

  deleteTable(id: number): Observable<ResultResponse> {
    return this.http.delete<ResultResponse>(`${this.apiUrl}/delete/${id}`);
  }
}
