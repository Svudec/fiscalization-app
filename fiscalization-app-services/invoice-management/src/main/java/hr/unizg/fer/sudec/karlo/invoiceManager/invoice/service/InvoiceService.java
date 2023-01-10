package hr.unizg.fer.sudec.karlo.invoiceManager.invoice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    public void handleFiscalizationResult(String fiscalizationResult){
        //TODO: handle result (error/success)
    }
}
