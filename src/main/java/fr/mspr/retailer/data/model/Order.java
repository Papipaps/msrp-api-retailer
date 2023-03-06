package fr.mspr.retailer.data.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @SequenceGenerator(
            name = "orders_sequence",
            sequenceName = "orders_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "orders_sequence",
            strategy = GenerationType.SEQUENCE)
    private Long id;
    private LocalDateTime createdAt;

    private int quantity;

    @ManyToOne(targetEntity = Product.class)
    @JsonBackReference(value = "product-reference")
    private Product product;

    @ManyToOne(targetEntity = Profile.class)
    @JsonBackReference(value = "profile-reference")
    private Profile profile;


}
