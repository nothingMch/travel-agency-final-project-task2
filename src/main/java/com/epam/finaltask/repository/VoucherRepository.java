package com.epam.finaltask.repository;

import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.VoucherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, UUID> {

    @Modifying
    @Transactional
    @Query("UPDATE Voucher v SET v.status = :newStatus WHERE v.id = :voucherId")
    int updateVoucherStatus(UUID voucherId, VoucherStatus newStatus);

    @Query("SELECT COUNT(v) > 0 FROM Voucher v WHERE v.id = :voucherId AND v.user.username = :username")
    boolean existsByUsernameAndVoucherId(@Param("username") String username, @Param("voucherId") UUID voucherId);

    Page<Voucher> findAllByUserId(UUID userId, Pageable pageable);

    Page<Voucher> findByStatus(VoucherStatus status, Pageable pageable);
}
