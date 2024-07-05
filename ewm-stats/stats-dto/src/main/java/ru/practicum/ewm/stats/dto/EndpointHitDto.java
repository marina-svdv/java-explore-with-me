package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {
    private Long id;

    @NotBlank(message = "App name cannot be blank")
    private String app;

    @NotBlank(message = "URI cannot be blank")
    private String uri;

    @NotBlank(message = "IP address cannot be blank")
    private String ip;

    @NotNull(message = "Timestamp cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public EndpointHitDto(String app, String uri, String ip, LocalDateTime localDateTime) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = localDateTime;
    }
}