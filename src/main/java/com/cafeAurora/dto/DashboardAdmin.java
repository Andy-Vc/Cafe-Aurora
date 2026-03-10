package com.cafeAurora.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardAdmin {

    private Long reservationsToday;
    private Long reservationsMonth;

    private Long pendingReservations;
    private Long confirmedReservations;
    private Long completedReservations;
    private Long cancelledReservations;
    private Long rejectdReservations;

    private Long totalClients;
    private Long totalTables;

    private List<Object[]> reservationsLastDays;

}