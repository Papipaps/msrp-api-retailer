package fr.mspr.retailer.data.dto;

import fr.mspr.retailer.data.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomerDTO {

    private long id;
    private LocalDateTime createdAt;
    private String name;
    private String username;
    private String firstName;
    private String lastName;
    private Adress address;
    private Company company ;
    private ArrayList<Order> orders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDTO that = (CustomerDTO) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
