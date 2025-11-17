import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { CustomerComponent } from "../layout/customer/customer.component";
import { IndexComponent } from "./index/index.component";
import { MenuComponent } from "./menu/menu.component";
import { GalleryComponent } from "./gallery/gallery.component";
import { ProfileComponent } from "./profile/profile.component";
import { ReservationComponent } from "./reservation/reservation.component";
import { authGuard } from "../guards/auth.guard"; // 👈 Importar el guard

const routes: Routes = [
  {
    path: '',
    component: CustomerComponent,
    children: [
      { path: '', redirectTo: 'index', pathMatch: 'full' },
      {
        path: 'index',
        component: IndexComponent,
        data: { title: 'Index' },
      },
      {
        path: 'menu',
        component: MenuComponent,
        data: { title: 'Menú' },
      },
      {
        path: 'gallery',
        component: GalleryComponent,
        data: { title: 'Galeria' },
      },
      {
        path: 'perfil',
        component: ProfileComponent,
        canActivate: [authGuard], 
        data: { title: 'Perfil', roles: ['CUSTOMER', 'C'] },
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
export class CustomerRoutingModule {}