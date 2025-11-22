export type TableStatus = 'RESERVADA' | 'OCUPADA' | 'DISPONIBLE';

export interface TableCoffe {
  idTable: number;
  tableNumber: number;
  capacity: number;
  location?: string;
  isAvailable: boolean;
  status: TableStatus;
  createdAt: string;
}
