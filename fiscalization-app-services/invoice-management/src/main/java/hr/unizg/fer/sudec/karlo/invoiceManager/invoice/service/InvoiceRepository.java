package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.service;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findInvoiceByInvoiceNumber(String invoiceNumber);
    boolean existsInvoiceByInvoiceNumber(String invoiceNumber);
    @Query("SELECT i FROM Invoice i JOIN i.invoiceItems ii WHERE ii.catalogItemId = :catalogItemId ORDER BY i.invoiceDate DESC")
    List<Invoice> findInvoicesByCatalogItemId(@Param("catalogItemId") Long catalogItemId);
    List<Invoice> findAllByOrderByInvoiceDateDesc();
}
