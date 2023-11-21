package com.ecommerce.payment_service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findBypaidauctionid(int paidauctionid);
}
