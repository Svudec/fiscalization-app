package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.service;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Invoice findInvoiceByInvoiceNumber(String invoiceNumber);
}
