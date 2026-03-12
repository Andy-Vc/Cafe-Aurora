import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AdminComponent } from '../layout/admin/admin.component';
import { CategoryCrudComponent } from './category-crud/category-crud.component';
import { ItemCrudComponent } from './item-crud/item-crud.component';
import { GalleryCrudComponent } from './gallery-crud/gallery-crud.component';
import { ReceptionistCrudComponent } from './receptionist-crud/receptionist-crud.component';
import { TableCrudComponent } from './table-crud/table-crud.component';
import { ReportReservationComponent } from './report-reservation/report-reservation.component';
import { ReportTableComponent } from './report-table/report-table.component';
import { ReportReceptionistComponent } from './report-receptionist/report-receptionist.component';

const routes: Routes = [
  {
    path: '',
    component: AdminComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: { title: 'Dashboard' },
      },
      {
        path: 'category',
        children: [
          {
            path: '',
            component: CategoryCrudComponent,
            data: { title: 'Categorias' },
          },
        ],
      },
      {
        path: 'item',
        children: [
          {
            path: '',
            component: ItemCrudComponent,
            data: { title: 'Productos' },
          },
        ],
      },
      {
        path: 'gallery',
        children: [
          {
            path: '',
            component: GalleryCrudComponent,
            data: { title: 'Galeria' },
          },
        ],
      },
      {
        path: 'receptionist',
        children: [
          {
            path: '',
            component: ReceptionistCrudComponent,
            data: { title: 'Recepcionista' },
          },
        ],
      },
      {
        path: 'table',
        children: [
          {
            path: '',
            component: TableCrudComponent,
            data: { title: 'Mesas' },
          },
        ],
      },
      {
        path: 'reports/reservations',
        children: [
          {
            path: '',
            component: ReportReservationComponent,
            data: { title: 'Reporte Reservas' },
          },
        ],
      },
      {
        path: 'reports/tables',
        children: [
          {
            path: '',
            component: ReportTableComponent,
            data: { title: 'Reporte Mesas Ocupadas' },
          },
        ],
      },
      {
        path: 'reports/receptionists',
        children: [
          {
            path: '',
            component: ReportReceptionistComponent,
            data: { title: 'Reporte Recepcionistas' },
          },
        ],
      },
      /*
      {
        path: 'reportes',
        children : [
            {path :'reporteStock', component : ReporteStockComponent},
            {path :'reporteVenta', component : ReporteVentaComponent},
          
        ]
      }
    */
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}
