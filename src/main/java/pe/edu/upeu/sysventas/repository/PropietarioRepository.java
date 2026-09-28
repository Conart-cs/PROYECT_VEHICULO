package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Propietario;

public class PropietarioRepository
        extends AbstractJpaRepository<Propietario, Long> {

    private long sequence = 1;

    @Override
    protected Long getId(Propietario entity) {
        return entity.getIdPropietario();
    }

    @Override
    protected void setId(Propietario entity, Long id) {
        entity.setIdPropietario(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }
}