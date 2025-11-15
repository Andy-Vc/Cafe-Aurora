import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AdminComponent } from '../layout/admin/admin.component';
import { CategoryCrudComponent } from './category-crud/category-crud.component';
import { ItemCrudComponent } from './item-crud/item-crud.component';
import { GalleryCrudComponent } from './gallery-crud/gallery-crud.component';

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
      }/*
      {
        path: 'reportes',
        children : [
            {path :'reporteStock', component : ReporteStockComponent},
            {path :'reporteVenta', component : ReporteVentaComponent},
          
        ]
      }
    */,
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}
