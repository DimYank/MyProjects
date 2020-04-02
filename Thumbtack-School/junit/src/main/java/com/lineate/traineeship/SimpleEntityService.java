package com.lineate.traineeship;

import java.util.Collection;
import java.util.HashSet;

public class SimpleEntityService extends EntityService {
    private final Collection<Entity> entities = new HashSet<>();

    protected Entity getEntity(String name) {
        return entities.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    protected void saveEntity(Entity entity) {
        entities.add(entity);
    }
}
