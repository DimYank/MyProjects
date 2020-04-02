package com.lineate.traineeship;

import java.util.Collection;

public class UserService {
    public User createUser(String name, Group group) {
        User user = new User(name, group);
        group.addUser(user);
        return user;
    }

    public Group createGroup(String name, Collection<Permission> permissions) {
        return new Group(name, permissions);
    }
}
