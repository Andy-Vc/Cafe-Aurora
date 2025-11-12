import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { LoginRequest } from '../../shared/dto/loginrequest';
import { CommonModule } from '@angular/common';
import { RoleRedirectService } from '../../guards/role-redirect.service';
import { AlertService } from '../../shared/util/sweet-alert';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [CommonModule,ReactiveFormsModule,RouterModule ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;
  loading: boolean = false;
  showPassword: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private roleRedirectService: RoleRedirectService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

 onSubmit() {
  if (this.loginForm.valid) {
    this.loading = true;

    const loginData: LoginRequest = {
      email: this.loginForm.value.email,
      password: this.loginForm.value.password
    };

    this.authService.login(loginData).subscribe({
      next: (response) => {
        console.log('Login exitoso:', response);     
        AlertService.success(response.message);    
        this.roleRedirectService.redirectByRole(response.role);
      },
      error: (err) => {
        console.error('Error en login:', err);
        AlertService.error(
          err.error?.message || 'Credenciales inválidas. Por favor, intenta de nuevo.'
        );
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });

  } else {
    Object.keys(this.loginForm.controls).forEach(key => {
      this.loginForm.get(key)?.markAsTouched();
    });
    AlertService.info('Por favor, completa todos los campos requeridos.');
  }
}
}