package com.lineate.traineeship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private ServiceFactory serviceFactory = new ServiceFactory();
    private UserService userService = serviceFactory.createUserService();

    @Test
    public void testCreateGroup(){
        List<Permission> permissions = Arrays.asList(Permission.read, Permission.write);
        Group group = userService.createGroup("test", permissions);

        assertNotNull(group.getName());
        assertFalse(group.getName().isEmpty());
        assertNotNull(group.getPermissions());
        assertEquals(2, group.getPermissions().size());
    }

    @Test
    public void testCreateUser(){
        List<Permission> permissions = Arrays.asList(Permission.read, Permission.write);
        Group group = userService.createGroup("test", permissions);
        User user = userService.createUser("vasya", group);

        assertNotNull(user.getName());
        assertFalse(user.getName().isEmpty());
        assertNotNull(user.getGroups());
        assertEquals(1, user.getGroups().size());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testCreateGroupFailNullAndEmptyName(String name){
        List<Permission> permissions = Arrays.asList(Permission.read, Permission.write);
        assertNull(userService.createGroup(name, permissions));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testCreateGroupFailNoAndEmptyPermissions(List<Permission> perm){
        assertNull(userService.createGroup("asd", perm));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testCreateUserFailNullAndEmptyName(String name){
        List<Permission> permissions = Arrays.asList(Permission.read, Permission.write);
        Group group = userService.createGroup("test", permissions);
        assertNull(userService.createUser(name, group));
    }

    @ParameterizedTest
    @NullSource
    public void testCreateUserFailNoGroups(Group group){
        assertNull(userService.createUser("ads", group));
    }
}
