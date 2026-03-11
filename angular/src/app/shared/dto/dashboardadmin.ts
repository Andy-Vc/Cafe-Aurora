export interface DashboardAdmin{
    reservationsToday: number;
    reservationsMonth: number;
    pendingReservations: number;
    confirmedReservations: number;
    completedReservations: number;
    cancelledReservations: number;
    rejectdReservations: number;
    noShosReservations:number;
    totalClients: number;
    totalTables: number;
    reservationsLastDays: [string, number][];
}