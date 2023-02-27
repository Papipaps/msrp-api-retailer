package fr.mspr.retailer.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude
public class RegistrationDTO {

    @NotBlank
    @Length(min = 6, max = 30)
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
    private String email;
    @NotBlank
    @Length(min = 2, max = 15)
    private String postalCode;
    @NotBlank
    @Length(min = 2, max = 40)
    private String city;

}
