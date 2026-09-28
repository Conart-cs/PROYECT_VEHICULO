package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.model.Infraccion;

public class InfraccionRepository
        extends AbstractJpaRepository<Infraccion, Long> {

    private long sequence = 1;

    @Override
    protected Long getId(Infraccion entity) {
        return entity.getIdInfraccion();
    }

    @Override
    protected void setId(Infraccion entity, Long id) {
        entity.setIdInfraccion(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }
}