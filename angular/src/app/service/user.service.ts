import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { User } from '../shared/model/user.model';
import { BehaviorSubject, Observable } from 'rxjs';
import { ResultResponse } from '../shared/dto/resultresponse';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/user`;
  private receptionistSubject = new BehaviorSubject<User[]>([]);
  public receptionist$ = this.receptionistSubject.asObservable();

  constructor(private http: HttpClient) {}

  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/id/${id}`);
  }

  /* CRUD RECEPCIONIST */
  allReceptionist(): void {
    this.http.get<User[]>(`${this.apiUrl}/list`).subscribe({
      next: (data) => this.receptionistSubject.next(data),
      error: (err) => console.error('Error fetching  recepcionist', err),
    });
  }
  createReceptionist(user: User): Observable<ResultResponse> {
    return this.http.post<ResultResponse>(`${this.apiUrl}/register`, user);
  }

  deleteReceptionist(id: string): Observable<ResultResponse> {
    return this.http.delete<ResultResponse>(`${this.apiUrl}/delete/${id}`);
  }
}
