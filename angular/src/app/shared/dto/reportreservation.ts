export interface ReportReservationDTO {
  idReservation: number;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  reservationDate: string;   
  reservationTime: string;   
  numPeople: number;
  status: string;
  source: string;
  tableNumber: number | null;
  tableLocation: string | null;
  specialNotes: string | null;
}