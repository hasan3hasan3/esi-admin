package com.example.esi.repository;

import com.example.esi.entity.Challan;
import com.example.esi.entity.PdfJson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdfJsonRepository extends JpaRepository<PdfJson, Long> {

    PdfJson findByPdfName(String pdfName);

}
