package com.cafeAurora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeAurora.model.Reservation;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Integer>{

}
