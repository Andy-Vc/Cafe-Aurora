import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { RegisterRequest } from '../shared/dto/registerrequest';
import { AuthResponse } from '../shared/dto/authresponse';
import { LoginRequest } from '../shared/dto/loginrequest';
import { UserResponse } from '../shared/dto/userresponse';
import { environment } from '../../environments/environment';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'auth_token';
  private currentUserSubject = new BehaviorSubject<UserResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private tokenService: TokenService,
  ) {
    const token = this.tokenService.getToken();
    if (token) {
      this.loadCurrentUser().subscribe({
        error: () => {
          this.logout();
        },
      });
    }
  }
  
  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/register`, request)
      .pipe(tap((response) => this.handleAuthResponse(response)));
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/login`, request)
      .pipe(tap((response) => this.handleAuthResponse(response)));
  }

  logout(): void {
    this.tokenService.removeToken();
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return this.tokenService.getToken();
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`);
  }

  getCurrentUserValue(): UserResponse | null {
    return this.currentUserSubject.value;
  }

  private loadCurrentUser(): Observable<any> {
    return this.getCurrentUser().pipe(
      tap((user) => this.currentUserSubject.next(user)),
    );
  }

  private handleAuthResponse(response: AuthResponse): void {
    if (response.token) {
      this.tokenService.setToken(response.token);

      const user: UserResponse = {
        idUser: response.userId,
        email: response.email,
        name: response.name,
        phone: response.phone,
        isActive: true,
        role: response.role,
      };

      this.currentUserSubject.next(user);
    }
  }
}
