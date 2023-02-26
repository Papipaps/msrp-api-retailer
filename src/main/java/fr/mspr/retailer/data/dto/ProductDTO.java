package fr.mspr.retailer.data.dto;

import fr.mspr.retailer.data.model.ProductDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductDTO extends BaseDTO {

    private long id;
    private LocalDateTime createdAt;
    private String name;
    private ProductDetails details;
    private int stock;


}
