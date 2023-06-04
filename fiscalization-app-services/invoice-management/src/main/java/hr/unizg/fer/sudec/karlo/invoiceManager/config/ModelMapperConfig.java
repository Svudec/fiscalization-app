package hr.unizg.fer.sudec.karlo.invoiceManager.config;

import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.entity.Invoice;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoice.model.InvoiceModel;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.entity.InvoiceItem;
import hr.unizg.fer.sudec.karlo.invoiceManager.invoiceItem.model.CatalogItemDTO;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    Converter<List<CatalogItemDTO>, List<InvoiceItem>> CatalogItemDTOToInvoiceItemConverter = mappingContext -> {
        Collection<CatalogItemDTO> source = mappingContext.getSource();
        return source.stream().map(i -> {
            InvoiceItem item = new InvoiceItem();
            item.setCatalogItemId(i.getCatalogItemId());
            item.setQuantity(i.getQuantity());
            return item;
        }).toList();
    };

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
//        modelMapper.addConverter(CatalogItemDTOToInvoiceItemConverter);
        modelMapper.createTypeMap(InvoiceModel.class, Invoice.class)
                .addMappings(mapper -> mapper.using(CatalogItemDTOToInvoiceItemConverter)
                        .map(InvoiceModel::getInvoiceItems, Invoice::setInvoiceItems));

        return modelMapper;
    }
}