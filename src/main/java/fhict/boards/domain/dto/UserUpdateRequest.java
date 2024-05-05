package fhict.boards.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = "Name is required.")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
    private String name;

    @Size(max = 50, message = "Job title must not exceed 50 characters.")
    private String jobTitle;

    @Size(max = 50, message = "Company must not exceed 50 characters.")
    private String company;

    @Size(max = 50, message = "Location must not exceed 50 characters.")
    private String location;
}
