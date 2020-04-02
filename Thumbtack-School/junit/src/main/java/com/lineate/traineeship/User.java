package com.lineate.traineeship;

import java.util.Collection;
import java.util.HashSet;

public class User {
    private final String name;
    private final Collection<Group> groups = new HashSet<>();

    public User(String name, Group group) {
        this.name = name;
        groups.add(group);
    }

    public String getName() {
        return name;
    }

    public Collection<Group> getGroups() {
        return groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }

        User user = (User) o;

        return name.equals(user.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
