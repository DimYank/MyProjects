package com.lineate.traineeship;

public interface EntityRepository {
    void save(Entity entity);

    Entity get(String name);
}
