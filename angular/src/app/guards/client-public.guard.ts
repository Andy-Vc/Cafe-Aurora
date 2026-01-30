// client-public.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { RoleRedirectService } from './role-redirect.service';
import { filter, map, take } from 'rxjs/operators';
import { of } from 'rxjs';

export const clientPublicGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const roleRedirectService = inject(RoleRedirectService);

  // Si no está autenticado, permitir acceso
  if (!authService.isAuthenticated()) {
    return true;
  }

  // Esperar a que el usuario esté cargado
  return authService.currentUser$.pipe(
    filter(user => user !== null), // Esperar hasta que haya usuario
    take(1),
    map(user => {
      // Si es cliente, permitir acceso
      if (user.role === 'C' || user.role === 'CUSTOMER') {
        return true;
      }

      // Si es admin o recepcionista, redirigir a su dashboard
      console.log('Bloqueando acceso a cliente para rol:', user.role);
      roleRedirectService.redirectByRole(user.role);
      return false;
    })
  );
};