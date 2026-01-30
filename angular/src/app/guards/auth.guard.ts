import { inject } from '@angular/core';
import { CanActivateFn, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { RoleRedirectService } from './role-redirect.service';
import { filter, map, take } from 'rxjs/operators';

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const authService = inject(AuthService);
  const roleRedirectService = inject(RoleRedirectService);

  if (!authService.isAuthenticated()) {
    roleRedirectService.redirectByRole(undefined);
    return false;
  }

  const allowedRoles = route.data['roles'] as string[];

  return authService.currentUser$.pipe(
    filter((user) => user !== null),
    take(1),
    map((user) => {
      if (
        allowedRoles &&
        !allowedRoles.includes(user.role.toUpperCase()) &&
        !allowedRoles.includes(user.role)
      ) {
        console.warn(
          'Acceso denegado. Rol requerido:',
          allowedRoles,
          'Rol actual:',
          user.role,
        );
        roleRedirectService.redirectByRole(user.role);
        return false;
      }
      return true;
    }),
  );
};
