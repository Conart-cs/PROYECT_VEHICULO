package pe.edu.upeu.sysventas.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Infraccion {

    private Long idInfraccion;

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "La gravedad es obligatoria")
    private String gravedad;

    @NotNull(message = "La multa es obligatoria")
    @Positive(message = "La multa debe ser mayor que cero")
    private Double multa;

    @NotBlank(message = "El vehículo es obligatorio")
    private String vehiculo;
}