package com.performance.benchmark.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class Invoice {
    @Id
    @GeneratedValue
    private Integer id;
    private String invoiceNumber;
    private Double amount;
    @CreationTimestamp
    private Date createdAt;
}
