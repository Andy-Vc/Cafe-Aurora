import { ReservationUserRef } from '../dto/reservationuserid';
import { TableCoffe } from './table.model';

export type ReservationStatus = 'PENDIENTE' | 'CONFIRMADA' | 'CANCELADA' | 'COMPLETADA';

export interface Reservation {
  idReservation?: number;
  user: ReservationUserRef;
  table?: TableCoffe;
  reservationDate: string;   
  reservationTime: string;  
  numPeople: number;
  customerName: string;
  customerPhone: string;
  customerEmail: string;
  specialNotes?: string;
  status: ReservationStatus;
  attendedBy?: ReservationUserRef;
  responseNotes?: string;
  createdAt: string;
  updatedAt: string;
}
