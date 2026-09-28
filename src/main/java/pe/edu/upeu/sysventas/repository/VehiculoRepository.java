package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Vehiculo;

public class VehiculoRepository extends AbstractJpaRepository<Vehiculo, Long> {

    private long sequence = 1;

    @Override
    protected Long getId(Vehiculo entity) {
        return entity.getIdVehiculo();
    }

    @Override
    protected void setId(Vehiculo entity, Long id) {
        entity.setIdVehiculo(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    public boolean Existe_Placa(String placa) {
        return data.stream()
                .anyMatch(vehiculo ->
                        vehiculo.getPlaca().equalsIgnoreCase(placa));
    }
}