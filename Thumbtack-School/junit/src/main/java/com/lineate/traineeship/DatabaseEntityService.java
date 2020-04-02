package com.lineate.traineeship;

public class DatabaseEntityService extends EntityService {
    private final EntityRepository entityRepository;

    public DatabaseEntityService(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    @Override
    protected Entity getEntity(String name) {
        return entityRepository.get(name);
    }

    @Override
    protected void saveEntity(Entity entity) {
        entityRepository.save(entity);
    }
}
