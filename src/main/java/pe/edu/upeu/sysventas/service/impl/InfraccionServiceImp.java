package pe.edu.upeu.sysventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.model.Infraccion;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.InfraccionRepository;
import pe.edu.upeu.sysventas.service.IInfraccionService;

@RequiredArgsConstructor
public class InfraccionServiceImp
        extends CrudGenericoServiceImp<Infraccion, Long>
        implements IInfraccionService {

    private final InfraccionRepository infraccionRepository;

    @Override
    protected ICrudGenericoRepository<Infraccion, Long> getRepo() {
        return infraccionRepository;
    }
}