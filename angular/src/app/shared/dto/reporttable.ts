export interface ReportTableDTO {
  tableNumber: number;
  location: string;
  capacity: number;
  totalReservations: number;
  confirmed: number;
  cancelled: number;
  noShow: number;
  completed: number;
}