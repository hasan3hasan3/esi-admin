package com.example.esi.repository;

import com.example.esi.entity.Challan;
import com.example.esi.entity.Contribution;
import com.example.esi.entity.HistoryTotal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallanRepository extends JpaRepository<Challan, Long> {

    //22001290260001099
    Challan findByPeriodAndEmployerCode(String period, String employerCode);

}
