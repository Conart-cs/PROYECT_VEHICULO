package pe.edu.upeu.sysventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.model.Propietario;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.PropietarioRepository;
import pe.edu.upeu.sysventas.service.IPropietarioService;

@RequiredArgsConstructor
public class PropietarioServiceImp
        extends CrudGenericoServiceImp<Propietario, Long>
        implements IPropietarioService {

    private final PropietarioRepository propietarioRepository;

    @Override
    protected ICrudGenericoRepository<Propietario, Long> getRepo() {
        return propietarioRepository;
    }
}