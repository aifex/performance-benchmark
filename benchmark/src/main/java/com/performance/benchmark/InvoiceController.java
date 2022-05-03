package com.performance.benchmark;

import com.performance.benchmark.model.Invoice;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceRepository invoiceRepository;

    @PostMapping("/invoices")
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice){
        return ResponseEntity.ok(invoiceRepository.save(invoice));
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<Invoice>> getInvoices(@RequestParam Integer limit,
                                                     @RequestParam Integer offset){
        return ResponseEntity.ok(invoiceRepository.findAll(PageRequest.of(offset, limit)).toList());
    }

    @GetMapping("/invoices/{invoiceNumber}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable String invoiceNumber){
        return ResponseEntity.ok(invoiceRepository.findByInvoiceNumber(invoiceNumber));
    }

    @GetMapping("/invoice-metrics")
    public ResponseEntity<InvoiceMetrics> getInvoiceMetrics(){
        InvoiceMetrics metrics = new InvoiceMetrics();
        List<Invoice> invoices = invoiceRepository.findAll(Sort.by("amount").ascending());

        if(invoices.isEmpty()){
            return ResponseEntity.ok(metrics);
        }

        metrics.setHighestAmount(invoices.get(invoices.size() - 1).getAmount());
        metrics.setLowestAmount(invoices.get(0).getAmount());
        metrics.setAverageAmount(invoices.stream().mapToDouble(Invoice::getAmount).average()
                .orElse(0.0));
        metrics.setTotalAmount(invoices.stream().mapToDouble(Invoice::getAmount).sum());

        return ResponseEntity.ok(metrics);
    }

    @Data
    static
    class InvoiceMetrics{
        private Double highestAmount, lowestAmount,  averageAmount, totalAmount;
    }

}
