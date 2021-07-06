package com.mihalsky.kitchen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mihalsky.kitchen.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

}
