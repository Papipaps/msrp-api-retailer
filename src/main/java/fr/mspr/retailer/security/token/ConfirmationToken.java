package fr.mspr.retailer.security.token;

import fr.mspr.retailer.data.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    private String token;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "profile_id"
    )
    private Profile profile;

    public ConfirmationToken(String token
            , LocalDateTime createdAt
            , LocalDateTime expiresAt
            , LocalDateTime confirmedAt
            , Profile profile) {

        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.confirmedAt = confirmedAt;
        this.profile = profile;
    }
}
