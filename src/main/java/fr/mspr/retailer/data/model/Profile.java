package fr.mspr.retailer.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    public Profile(String password, String username, Collection<Role> roles) {
        this.password = password;
        this.username = username;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank
    @Length(min = 6, max = 30)
    @Column(unique = true)
    private String username;
    @NotBlank
    @Length(min = 2, max = 40)
    private String firstName;
    @NotBlank
    @Length(min = 2, max = 40)
    private String lastName;
    @NotBlank
    @Length(max = 60)
    @Email
    @Column(unique = true)
    private String email;
    @NotBlank
    @Length(min = 2, max = 15)
    private String postalCode;
    @NotBlank
    @Length(min = 2, max = 40)
    private String city;

    @Length(min = 6)
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleEnum roles;
    @OneToMany(targetEntity = Order.class, fetch = FetchType.LAZY)
    private Collection<Order> orders;
    private LocalDateTime createdAt;

    private String companyName;

    private boolean isActive;



}
