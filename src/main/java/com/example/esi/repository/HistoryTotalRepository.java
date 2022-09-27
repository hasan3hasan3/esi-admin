package com.example.esi.repository;


import com.example.esi.entity.Challan;
import com.example.esi.entity.Contribution;
import com.example.esi.entity.HistoryTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface HistoryTotalRepository extends JpaRepository<HistoryTotal, Long> {

    //22001290260001099
    HistoryTotal findByPeriodAndEmployerCode(String period, String employerCode);

    HistoryTotal findByPdfViewDateAndEmployerCode(String pdfViewDate, String employerCode);


}
