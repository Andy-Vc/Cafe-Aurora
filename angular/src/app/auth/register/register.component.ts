import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { RoleRedirectService } from '../../guards/role-redirect.service';
import { RegisterRequest } from '../../shared/dto/registerrequest';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AlertService } from '../../shared/util/sweet-alert';
import { createClient } from '@supabase/supabase-js';
import { environment } from '../../../environments/environment';
import { CompleteGoogleProfileRequest } from '../../shared/dto/completegooglerequest';

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  loadingGoogle = false;
  showPassword = false;
  showConfirmPassword = false;

  showCompleteProfileModal = false;
  completeProfileForm: FormGroup;
  pendingGoogleToken = '';
  showModalPassword = false;
  pendingGoogleUserId = '';
  private googleCallbackProcessed = false;
  private supabase = createClient(
    environment.supabaseUrl,
    environment.supabaseAnonKey,
  );

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private roleRedirectService: RoleRedirectService,
  ) {
    this.registerForm = this.fb.group(
      {
        name: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        phone: ['', [Validators.required, Validators.pattern(/^[0-9]{9}$/)]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: this.passwordMatchValidator },
    );

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

  // ===== GOOGLE =====
  async registerWithGoogle() {
    this.loadingGoogle = true;
    try {
      await this.supabase.auth.signInWithOAuth({
        provider: 'google',
        options: { redirectTo: window.location.origin + '/auth/register' },
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
          err.error?.message || 'Error al registrarse con Google',
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
        error: (err) =>
          AlertService.error(err.error?.message || 'Error al completar perfil'),
      });
  }

  closeModal() {
    this.showCompleteProfileModal = false;
    this.pendingGoogleToken = '';
    this.pendingGoogleUserId = '';
    this.completeProfileForm.reset();
    this.supabase.auth.signOut();
  }

  toggleModalPassword() {
    this.showModalPassword = !this.showModalPassword;
  }

  passwordMatchValidator(group: FormGroup) {
    const pass = group.get('password')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return pass === confirm ? null : { passwordMismatch: true };
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }
  toggleConfirmPassword() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmit() {
    if (this.registerForm.valid) {
      this.loading = true;
      const registerData: RegisterRequest = {
        name: this.registerForm.value.name,
        email: this.registerForm.value.email,
        phone: this.registerForm.value.phone,
        password: this.registerForm.value.password,
      };
      this.authService.register(registerData).subscribe({
        next: (response) => {
          AlertService.success(response.message);
          setTimeout(
            () => this.roleRedirectService.redirectByRole(response.role),
            2000,
          );
        },
        error: (err) => {
          AlertService.error(err.error?.message || 'Error al registrarse.');
          this.loading = false;
        },
      });
    } else {
      Object.keys(this.registerForm.controls).forEach((k) =>
        this.registerForm.get(k)?.markAsTouched(),
      );
    }
  }

  get passwordMismatch() {
    return (
      this.registerForm.hasError('passwordMismatch') &&
      this.registerForm.get('confirmPassword')?.touched
    );
  }

  private extractUserIdFromToken(token: string): string {
    try {
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      return decoded.sub;
    } catch {
      return '';
    }
  }
}
