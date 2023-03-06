package fr.mspr.retailer.data.dto;

import fr.mspr.retailer.data.model.Product;
import fr.mspr.retailer.data.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO extends BaseDTO {

    private Long id;
    private LocalDateTime createdAt;
    private int quantity;

    private Long productId;

    private Long customerId;


}
