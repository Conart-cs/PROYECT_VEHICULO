package pe.edu.upeu.sysventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.model.Vehiculo;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.VehiculoRepository;
import pe.edu.upeu.sysventas.service.IVehiculoService;

@RequiredArgsConstructor
public class
VehiculoServiceImp extends CrudGenericoServiceImp<Vehiculo, Long>
        implements IVehiculoService {

    private final VehiculoRepository vehiculoRepository;

    @Override
    protected ICrudGenericoRepository<Vehiculo, Long> getRepo() {
        return vehiculoRepository;
    }

    @Override
    public boolean Existe_Placa(String placa) {
        return vehiculoRepository.Existe_Placa(placa);
    }
}