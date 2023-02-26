package fr.mspr.retailer.data.dto;

import fr.mspr.retailer.data.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

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

}
