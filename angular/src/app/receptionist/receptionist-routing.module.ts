import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CustomerComponent } from '../layout/customer/customer.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ReceptionistComponent } from '../layout/receptionist/receptionist.component';
import { ReservationComponent } from './reservation/reservation.component';

const routes: Routes = [
  {
    path: '',
    component: ReceptionistComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: { title: 'Dashboard' },
      },
      {
        path: 'reserva',
        component: ReservationComponent,
        data: { title: 'Reserva' },
      }
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReceptionistRoutingModule {}
