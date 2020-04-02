package com.lineate.traineeship;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class EntityService {

    public boolean createEntity(User user, String name, String value) {
        if (StringUtils.isEmpty(name)
                || name.contains(" ")
                || name.length() > 32) {
            return false;
        }
        Entity entity = new Entity(user, name);
        entity.setValue(value);
        saveEntity(entity);
        return true;
    }

    public String getEntityValue(User user, String name) {
        Entity entity = getEntity(name);
        return entity == null || !hasAccess(user, entity, Permission.read) ? null : entity.getValue();
    }

    public boolean updateEntity(User user, String name, String value) {
        Entity entity = getEntity(name);
        if (!hasAccess(user, entity, Permission.write)) {
            return false;
        }
        entity.setValue(value);
        return true;
    }

    protected abstract Entity getEntity(String name);

    protected abstract void saveEntity(Entity entity);

    private boolean hasAccess(final User user, Entity entity, Permission permission) {
        if (entity.getOwner().equals(user)) {
            return true;
        }
        Collection<Group> groups = entity.getGroups().stream()
                .filter(g -> user.getGroups().contains(g))
                .filter(g -> g.getPermissions().contains(permission))
                .collect(Collectors.toSet());
        return !groups.isEmpty();
    }
}
