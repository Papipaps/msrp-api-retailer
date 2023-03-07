package fr.mspr.retailer.data.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @SequenceGenerator(
            name = "profile_sequence",
            sequenceName = "profile_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "profile_sequence",
            strategy = GenerationType.SEQUENCE)
    private Long id;
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

    @Enumerated(EnumType.STRING)
    private RoleEnum roles;
    private LocalDateTime createdAt;
    @NotBlank
    @Length(min = 2, max = 40)
    private String companyName;

    private boolean isActive;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "profile")
    @JsonManagedReference(value = "profile-reference")
    private Set<Order> orders;


}
