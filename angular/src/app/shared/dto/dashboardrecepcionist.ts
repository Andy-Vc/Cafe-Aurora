import { Reservation } from "../model/reservation.model";

export interface DashboardRecepcionist{
    totalReservations: number,
    confirmedToday: number,
    pending: number,
    completedToday: number,
    availableTables: number,
    nextReservations: Reservation[]
}