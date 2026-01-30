import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { authPublicGuard } from './guards/auth-public.guard';
import { clientPublicGuard } from './guards/client-public.guard';

export const routes: Routes = [
  {
    path: 'auth',
    canActivate: [authPublicGuard],
    loadChildren: () => import('./auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: 'cliente',
    canActivate: [clientPublicGuard],
    loadChildren: () =>
      import('./customer/customer.module').then((m) => m.CustomerModule),
  },
  {
    path: 'admin',
    canActivate: [authGuard],
    data: { roles: ['ADMIN', 'A'] },
    loadChildren: () =>
      import('./admin/admin.module').then((m) => m.AdminModule),
  },
  {
    path: 'recepcionista',
    canActivate: [authGuard],
    data: { roles: ['RECEPTIONIST', 'R'] },
    loadChildren: () =>
      import('./receptionist/receptionist.module').then(
        (m) => m.ReceptionistModule,
      ),
  },
  { path: '', redirectTo: 'cliente/index', pathMatch: 'full' },

  { path: '**', redirectTo: 'cliente/index' },
];
