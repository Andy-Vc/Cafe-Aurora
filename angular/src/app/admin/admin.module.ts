import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";
import { AdminRoutingModule } from "./admin-routing.module";

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    AdminRoutingModule
  ]
})

export class AdminModule {}