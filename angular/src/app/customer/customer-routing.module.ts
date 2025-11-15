import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { CustomerComponent } from "../layout/customer/customer.component";
import { IndexComponent } from "./index/index.component";

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
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CustomerRoutingModule {}
