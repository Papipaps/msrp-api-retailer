package fr.mspr.retailer.security.token;

import fr.mspr.retailer.data.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ConfirmationToken {
    @Id
    @SequenceGenerator(
            sequenceName = "token_sequence"
            , name = "token_sequence"
            , allocationSize = 1)
    @GeneratedValue(
            generator = "token_sequence"
            , strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false)
    @Length(min = 16, max = 64)
    private String token;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.REMOVE,orphanRemoval = true)
    @JoinColumn(
            nullable = false,
            name = "profile_id"
    )
    private Profile profile;

}
