package com.example.esi.pojo;

import com.example.esi.entity.Contribution;
import com.example.esi.entity.HistoryTotal;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class ViewContributionHistoryData {

    HistoryTotal historyTotal;
    Page<Contribution> contribution;
}
