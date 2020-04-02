package com.lineate.traineeship;

public class ServiceFactory {
    public UserService createUserService() {
        return new UserService();
    }

    public EntityService createEntityService() {
        return new SimpleEntityService();
    }

    public EntityService createEntityService(EntityRepository entityRepository) {
        return new DatabaseEntityService(entityRepository);
    }
}
