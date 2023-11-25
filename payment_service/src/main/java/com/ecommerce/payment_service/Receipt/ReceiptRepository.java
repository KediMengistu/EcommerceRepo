package com.ecommerce.payment_service.Receipt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {
    Optional<Receipt> findByauctionid(int auctionid);
}
