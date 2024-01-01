package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Gateway;



@Repository
public interface GatewayRepository extends JpaRepository<Gateway, Long>{

}
