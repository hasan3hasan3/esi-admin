package com.example.esi.repository;


import com.example.esi.entity.Contribution;
import com.example.esi.entity.HistoryTotal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    Page<Contribution> findByHistoryTotal(HistoryTotal historyTotal, Pageable pageable);
}
