package com.performance.benchmarkreactive;

import com.performance.benchmarkreactive.model.Invoice;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.math.MathFlux;

@RestController
@RequiredArgsConstructor
public class ReactiveInvoiceController {
    private final ReactiveInvoiceRepository invoiceRepository;

    @PostMapping("/invoices")
    public Mono<Invoice> createInvoice(@RequestBody Invoice invoice){
        return invoiceRepository.save(invoice);
    }

    @GetMapping("/invoices")
    public Flux<Invoice> getInvoices(){
        return invoiceRepository.findAll();
    }

    @GetMapping("/invoices/{invoiceNumber}")
    public Mono<Invoice> getInvoice(@PathVariable String invoiceNumber){
        return invoiceRepository.findByInvoiceNumber(invoiceNumber);
    }

    @GetMapping("/invoice-metrics")
    public Mono<InvoiceMetrics> getInvoiceMetrics(){
        InvoiceMetrics metrics = new InvoiceMetrics();
        Flux<Invoice> invoices = invoiceRepository.findAll(Sort.by("amount").ascending());

        metrics.setHighestAmount(invoices.last().flatMap(x-> Mono.just(x.getAmount())).block());
        metrics.setLowestAmount(invoices.elementAt(0).flatMap(x-> Mono.just(x.getAmount())).block());
        metrics.setAverageAmount(MathFlux.averageDouble(invoices.flatMap(x-> Mono.just(x.getAmount()))).block());
        metrics.setTotalAmount(MathFlux.sumDouble(invoices.flatMap(x-> Mono.just(x.getAmount()))).block());

        return Mono.just(metrics);
    }

    @Data
    static
    class InvoiceMetrics{
        private Double highestAmount, lowestAmount,  averageAmount, totalAmount;
    }

}
