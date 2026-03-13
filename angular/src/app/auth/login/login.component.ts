import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { LoginRequest } from '../../shared/dto/loginrequest';
import { CommonModule } from '@angular/common';
import { RoleRedirectService } from '../../guards/role-redirect.service';
import { AlertService } from '../../shared/util/sweet-alert';
import { RouterModule } from '@angular/router';
import { createClient } from '@supabase/supabase-js';
import { environment } from '../../../environments/environment';
import { CompleteGoogleProfileRequest } from '../../shared/dto/completegooglerequest';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  loadingGoogle = false;
  showPassword = false;

  showCompleteProfileModal = false;
  completeProfileForm: FormGroup;
  pendingGoogleToken = '';
  pendingGoogleUserId = '';
  showModalPassword = false;

  private supabase = createClient(
    environment.supabaseUrl,
    environment.supabaseAnonKey,
  );
  private googleCallbackProcessed = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private roleRedirectService: RoleRedirectService,
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });

    this.completeProfileForm = this.fb.group({
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{9,15}$/)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });

    const url = window.location.href;
    const isOAuthCallback =
      url.includes('access_token') || url.includes('code=');
    if (!isOAuthCallback) {
      this.googleCallbackProcessed = true;
    }

    this.supabase.auth.onAuthStateChange((event, session) => {
      if (event === 'SIGNED_IN' && session && !this.googleCallbackProcessed) {
        this.googleCallbackProcessed = true;
        this.handleGoogleCallback(session.access_token);
      }
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }
  toggleModalPassword() {
    this.showModalPassword = !this.showModalPassword;
  }

  async loginWithGoogle() {
    this.loadingGoogle = true;
    try {
      await this.supabase.auth.signInWithOAuth({
        provider: 'google',
        options: {
          redirectTo: window.location.origin + '/auth/login',
        },
      });
    } catch {
      AlertService.error('Error al conectar con Google');
      this.loadingGoogle = false;
    }
  }

  private handleGoogleCallback(accessToken: string) {
    this.loadingGoogle = true;
    this.authService.loginWithGoogle(accessToken).subscribe({
      next: (response: any) => {
        if (response.message === 'incomplete_profile') {
          this.pendingGoogleToken = response.token || accessToken;
          this.pendingGoogleUserId = this.extractUserIdFromToken(
            this.pendingGoogleToken,
          );
          this.showCompleteProfileModal = true;
          this.loadingGoogle = false;
          return;
        }
        AlertService.success(response.message);
        this.roleRedirectService.redirectByRole(response.role);
        this.loadingGoogle = false;
      },
      error: (err) => {
        AlertService.error(
          err.error?.message || 'Error al iniciar sesión con Google',
        );
        this.loadingGoogle = false;
      },
      complete: () => {
        this.loadingGoogle = false;
      },
    });
  }

  submitCompleteProfile() {
    if (this.completeProfileForm.invalid) {
      Object.keys(this.completeProfileForm.controls).forEach((k) =>
        this.completeProfileForm.get(k)?.markAsTouched(),
      );
      return;
    }

    const request: CompleteGoogleProfileRequest = {
      ...this.completeProfileForm.value,
      userId: this.pendingGoogleUserId,
    };
    this.authService
      .completeGoogleProfile(this.pendingGoogleToken, request)
      .subscribe({
        next: (response) => {
          this.showCompleteProfileModal = false;
          AlertService.success(response.message);
          this.roleRedirectService.redirectByRole(response.role);
        },
        error: (err) => {
          AlertService.error(err.error?.message || 'Error al completar perfil');
        },
      });
  }

  closeModal() {
    this.showCompleteProfileModal = false;
    this.pendingGoogleToken = '';
    this.pendingGoogleUserId = '';
    this.completeProfileForm.reset();
    this.supabase.auth.signOut();
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.loading = true;
      const loginData: LoginRequest = {
        email: this.loginForm.value.email,
        password: this.loginForm.value.password,
      };
      this.authService.login(loginData).subscribe({
        next: (response) => {
          AlertService.success(response.message);
          this.roleRedirectService.redirectByRole(response.role);
        },
        error: (err) => {
          AlertService.error(err.error?.message || 'Credenciales inválidas.');
          this.loading = false;
        },
        complete: () => {
          this.loading = false;
        },
      });
    } else {
      Object.keys(this.loginForm.controls).forEach((k) =>
        this.loginForm.get(k)?.markAsTouched(),
      );
      AlertService.info('Por favor, completa todos los campos requeridos.');
    }
  }
  private extractUserIdFromToken(token: string): string {
    try {
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      console.log('=== JWT decoded:', decoded);
      console.log('=== sub:', decoded.sub);
      return decoded.sub;
    } catch (e) {
      console.log('=== Error decodificando:', e);
      return '';
    }
  }
}
