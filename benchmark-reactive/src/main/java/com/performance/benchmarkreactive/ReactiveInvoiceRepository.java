package com.performance.benchmarkreactive;

import com.performance.benchmarkreactive.model.Invoice;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;


public interface ReactiveInvoiceRepository extends ReactiveSortingRepository<Invoice, Integer> {
    Mono<Invoice> findByInvoiceNumber(String invoiceNumber);
}
