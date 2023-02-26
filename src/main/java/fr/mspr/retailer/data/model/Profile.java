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
    @Length(min = 6)
    private String password;
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private RoleEnum roles;
    @OneToMany(targetEntity = Order.class, fetch = FetchType.LAZY)
    private Collection<Order> orders;
    private LocalDateTime createdAt;
    private String firstName;
    private String lastName;
    private String postalCode;
    private String city;
    private String companyName;

    private boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return id == profile.id && Objects.equals(password, profile.password) && username.equals(profile.username) && roles == profile.roles && Objects.equals(orders, profile.orders) && Objects.equals(createdAt, profile.createdAt) && Objects.equals(firstName, profile.firstName) && Objects.equals(lastName, profile.lastName) && Objects.equals(postalCode, profile.postalCode) && Objects.equals(city, profile.city) && Objects.equals(companyName, profile.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, password, username, roles, orders, createdAt, firstName, lastName, postalCode, city, companyName);
    }
}
