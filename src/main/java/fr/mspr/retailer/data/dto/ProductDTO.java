package fr.mspr.retailer.data.dto;

import fr.mspr.retailer.data.model.ProductDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductDTO extends BaseDTO {

    private Long id;
    private LocalDateTime createdAt;
    private String name;
    private ProductDetails details;
    private int stock;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProductDTO that = (ProductDTO) o;
        return stock == that.stock && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, details, stock);
    }
}
