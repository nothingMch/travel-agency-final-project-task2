package com.epam.finaltask.repository;

import java.util.List;
import java.util.UUID;

import com.epam.finaltask.model.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, UUID>, JpaSpecificationExecutor<Tour> {

    @Query("SELECT t FROM Tour t JOIN Voucher v ON t.id = v.tour.id WHERE v.user.id = :userId")
    Page<Tour> findAllByUserId(UUID userId, Pageable pageable);

    Page<Tour> findAllByTourType(TourType tourType, Pageable pageable);

    Page<Tour> findAllByTransferType(TransferType transferType, Pageable pageable);

    Page<Tour> findByPriceGreaterThanEqual(Double price, Pageable pageable);

    Page<Tour> findAllByHotelType(HotelType hotelType, Pageable pageable);

    Page<Tour> findByTitleContainingIgnoreCase(String titlePart, Pageable pageable);

}
