import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";
import { CustomerRoutingModule } from "./customer-routing.module";

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    CustomerRoutingModule
  ]
})

export class CustomerModule {}