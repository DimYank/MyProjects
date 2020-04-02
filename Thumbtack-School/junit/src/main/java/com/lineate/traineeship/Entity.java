package com.lineate.traineeship;

import java.util.Collection;

public class Entity {
    private final User owner;
    private final Collection<Group> groups;

    private final String name;
    private String value;

    public Entity(User owner, String name) {
        this.owner = owner;
        groups = owner.getGroups();
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Collection<Group> getGroups() {
        return groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Entity)) {
            return false;
        }

        Entity entity = (Entity) o;

        return name.equals(entity.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
