package com.tradeshift.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tradeshift.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}

