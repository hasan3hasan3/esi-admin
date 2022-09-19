package com.example.esi.repository;

import com.example.esi.entity.Challan;
import com.example.esi.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails, Long> {
    TransactionDetails findByChallanNumberAndEmployersCodeNo(String challanNumber, String employerCode);

}
