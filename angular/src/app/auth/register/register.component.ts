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

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading: boolean = false;
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private roleRedirectService: RoleRedirectService
  ) {
    this.registerForm = this.fb.group(
      {
        name: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        phone: ['', [Validators.required, Validators.pattern(/^[0-9]{9}$/)]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  passwordMatchValidator(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
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
          setTimeout(() => {
            this.roleRedirectService.redirectByRole(response.role);
          }, 2000);
        },
        error: (err) => {
          AlertService.error(
            err.error?.message ||
              'Credenciales inválidas. Por favor, intenta de nuevo.'
          );
          this.loading = false;
        },
      });
    } else {
      Object.keys(this.registerForm.controls).forEach((key) => {
        this.registerForm.get(key)?.markAsTouched();
      });
    }
  }

  get passwordMismatch() {
    return (
      this.registerForm.hasError('passwordMismatch') &&
      this.registerForm.get('confirmPassword')?.touched
    );
  }
}
