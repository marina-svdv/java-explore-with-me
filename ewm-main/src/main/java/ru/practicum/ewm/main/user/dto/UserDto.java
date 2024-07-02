package ru.practicum.ewm.main.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email must not be blank")
    @Size(min = 6, max = 254, message = "Email must be more then 6 and less than 250 characters")
    private String email;

    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 250, message = "Name must be more then 2 and less than 250 characters")
    private String name;
}