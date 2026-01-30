import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { RoleRedirectService } from './role-redirect.service';
import { map, take } from 'rxjs/operators';

export const authPublicGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const roleRedirectService = inject(RoleRedirectService);

  if (!authService.isAuthenticated()) {
    return true;
  }

  const currentUser = authService.getCurrentUserValue();
  
  if (currentUser?.role) {
    roleRedirectService.redirectByRole(currentUser.role);
    return false;
  }

  return authService.currentUser$.pipe(
    take(1),
    map(user => {
      if (user) {
        roleRedirectService.redirectByRole(user.role);
        return false;
      }
      return true;
    })
  );
};