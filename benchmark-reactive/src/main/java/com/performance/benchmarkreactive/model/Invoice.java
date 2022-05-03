package com.performance.benchmarkreactive.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Invoice {
    @Id
    private Integer id;
    private String invoiceNumber;
    private Double amount;
}
