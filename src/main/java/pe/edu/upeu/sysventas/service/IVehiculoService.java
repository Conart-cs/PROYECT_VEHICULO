package pe.edu.upeu.sysventas.service;

import pe.edu.upeu.sysventas.model.Vehiculo;

public interface IVehiculoService extends ICrudGenericoService<Vehiculo, Long> {

    boolean Existe_Placa(String placa);
}