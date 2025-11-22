import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";
import {  ReceptionistRoutingModule } from "./receptionist-routing.module";

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    ReceptionistRoutingModule
  ]
})

export class ReceptionistModule {}