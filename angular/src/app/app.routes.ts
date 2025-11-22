import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: 'cliente',
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
        (m) => m.ReceptionistModule
      ),
  },
  { path: '', redirectTo: 'cliente/index', pathMatch: 'full' },

  { path: '**', redirectTo: 'auth/login' },
];
