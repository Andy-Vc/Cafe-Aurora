import { TableCoffe } from './table.model';
import { User } from './user.model';

export type ReservationStatus = 'PENDIENTE' | 'CONFIRMADA' | 'CANCELADA' | 'COMPLETADA';

export interface Reservation {
  idReservation: number;
  user: User;
  table: TableCoffe;
  reservationDate: string;   
  reservationTime: string;  
  numPeople: number;
  customerName: string;
  customerPhone: string;
  customerEmail: string;
  specialNotes?: string;
  status: ReservationStatus;
  attendedBy?: User;
  responseNotes?: string;
  createdAt: string;
  updatedAt: string;
}
