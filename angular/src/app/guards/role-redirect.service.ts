import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class RoleRedirectService {
  constructor(private router: Router) {}
  /*Falta mejorar los guards */
  redirectByRole(role: string | undefined) {
    if (!role) {
      this.router.navigate(['/login']);
      return;
    }

    switch (role.toUpperCase()) {
      case 'ADMIN':
      case 'A':
        this.router.navigate(['/admin/dashboard']);
        break;
      case 'RECEPTIONIST':
      case 'R':
        this.router.navigate(['/recepcion/dashboard']);
        break;
      case 'CUSTOMER':
      case 'C':
        this.router.navigate(['/cliente/inicio']);
        break;
      default:
        console.warn('Rol desconocido:', role);
        this.router.navigate(['/login']);
        break;
    }
  }

  getDefaultRouteByRole(role: string | undefined): string {
    if (!role) return '/login';

    switch (role.toUpperCase()) {
      case 'ADMIN':
      case 'A':
        return '/admin/dashboard';
      case 'RECEPTIONIST':
      case 'R':
        return '/recepcion/dashboard';
      case 'CUSTOMER':
      case 'C':
        return '/cliente/inicio';
      default:
        return '/login';
    }
  }
}
