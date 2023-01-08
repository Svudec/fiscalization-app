package hr.unizg.fer.sudec.karlo.fiscalizationService.controller;

import hr.unizg.fer.sudec.karlo.fiscalizationService.service.FiscalizationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/fiscalization")
@AllArgsConstructor
public class FiscalizationController {

    private final FiscalizationService fiscalizationService;

    @PostMapping
    public void fiscalize(@RequestBody String invoice){
        log.info("Fiscalizating invoice {}", invoice);
        fiscalizationService.fiscalizeInvoice(invoice);
    }
}
