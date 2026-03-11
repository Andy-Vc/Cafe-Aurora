import { ReservationStatus } from "../model/reservation.model";

export interface UpdateReservationStatusRequest{
    idReservation:number;
    idRecepcionista: string;
    status?:ReservationStatus;
}