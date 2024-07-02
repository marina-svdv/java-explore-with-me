package ru.practicum.ewm.main.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    @NotNull
    private double lat;

    @NotNull
    private double lon;
}