import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then((m) => m.AuthModule),
  },
  
  {
    path: 'cliente',
    canActivate: [authGuard],
    data: { roles: ['CUSTOMER', 'C'] },
    loadChildren: () =>
      import('./customer/customer.module').then((m) => m.CustomerModule),
  },
  {
    path: 'admin',
    canActivate: [authGuard],
    data: { roles: ['ADMIN', 'A'] },
    loadChildren: () =>
      import('./admin/admin.module').then((m) => m.AdminModule),
  } /*
  {
    path: 'vendedor',
    loadChildren: () =>
      import('./vendedor/vendedor.module').then((m) => m.VendedorModule),
  },
  {
    path: 'repartidor',
    loadChildren: () =>
      import('./repartidor/repartidor.module').then((m) => m.RepartidorModule),
  },*/,
  { path: '', redirectTo: 'cliente/index', pathMatch: 'full' },

  { path: '**', redirectTo: 'auth/login' },
];
