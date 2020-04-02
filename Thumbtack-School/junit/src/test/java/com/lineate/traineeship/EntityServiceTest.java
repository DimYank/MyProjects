package com.lineate.traineeship;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class EntityServiceTest {

    private ServiceFactory serviceFactory = new ServiceFactory();
    private EntityService entityService = serviceFactory.createEntityService();
    private UserService userService = serviceFactory.createUserService();

    @Test
    public void testCreateEntity(){
        Group group = userService.createGroup("test", Arrays.asList(Permission.read, Permission.write));
        User user = userService.createUser("vasya", group);
        assertTrue(entityService.createEntity(user, "ent", "adasd"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testCreateEntityWithEmptyValue(String val){
        Group group = userService.createGroup("test", Arrays.asList(Permission.read, Permission.write));
        User user = userService.createUser("vasya", group);
        assertTrue(entityService.createEntity(user, "ent", val));
    }

    @Test
    public void testGetEntityValueAndReadByOwner(){
        Group group = userService.createGroup("test", Collections.singletonList(Permission.write));
        User user = userService.createUser("vasya", group);
        entityService.createEntity(user, "ent", "enttest");
        assertEquals( "enttest",entityService.getEntityValue(user, "ent"));
    }

    @Test
    public void testFailGetEntityWithNoUser(){
        Group group = userService.createGroup("test", Collections.singletonList(Permission.write));
        User user = userService.createUser("vasya", group);
        entityService.createEntity(user, "ent", "enttest");
        entityService.getEntityValue(null, "ent");
    }

    @Test
    public void testFailGetEntryWithNoRights(){
        Group group = userService.createGroup("test", Collections.singletonList(Permission.write));
        User user = userService.createUser("vasya", group);
        entityService.createEntity(user, "ent", "enttest");
        Group group2 = userService.createGroup("test2", Collections.singletonList(Permission.write));
        User user2 = userService.createUser("vasya2", group2);
        assertNull(entityService.getEntityValue(user2, "ent"));
    }

    @Test
    public void testUpdateEntityAndWriteByOwner(){
        Group group = userService.createGroup("test", Collections.singletonList(Permission.read));
        User user = userService.createUser("vasya", group);
        entityService.createEntity(user, "ent", "enttest");
        assertTrue(entityService.updateEntity(user,"ent", "enttest2"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testUpdateEntityWithEmptyValue(String val){
        Group group = userService.createGroup("test", Arrays.asList(Permission.read, Permission.write));
        User user = userService.createUser("vasya", group);
        entityService.createEntity(user, "ent", "enttest");
        assertTrue(entityService.updateEntity(user,"ent", val));
    }

    @Test
    public void testUpdateEntityAndGet(){
        Group group = userService.createGroup("test", Arrays.asList(Permission.read, Permission.write));
        User user = userService.createUser("vasya", group);
        entityService.createEntity(user, "ent", "enttest");
        entityService.updateEntity(user,"ent", "enttest2");
        assertEquals( "enttest2",entityService.getEntityValue(user, "ent"));
    }

    @Test
    public void testFailCreateEntityWithNoUser(){
        assertFalse(entityService.createEntity(null, "ent", "adasd"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"very_long_name__________________________________________________", "spaces __", ""})
    public void testFailCreateEntityWithWrongNames(String name){
        Group group = userService.createGroup("test", Arrays.asList(Permission.read, Permission.write));
        User user = userService.createUser("vasya", group);
        assertFalse(entityService.createEntity(user, name, "adasd"));
    }

    @Test
    public void testReadBySameGroup(){
        Group group = userService.createGroup("test", Arrays.asList(Permission.read, Permission.write));
        User user1 = userService.createUser("vasya", group);
        User user2 = userService.createUser("vova", group);
        entityService.createEntity(user1, "ent", "test");
        assertEquals("test", entityService.getEntityValue(user2, "ent"));
    }

    @Test
    public void testWriteBySameGroup(){
        Group group = userService.createGroup("test", Arrays.asList(Permission.read, Permission.write));
        User user1 = userService.createUser("vasya", group);
        User user2 = userService.createUser("vova", group);
        entityService.createEntity(user1, "ent", "test");
        assertTrue(entityService.updateEntity(user2, "ent", "update"));
    }

    @Test
    public void testFailReadBySameGroup(){
        Group group = userService.createGroup("test", Collections.singletonList(Permission.write));
        User user1 = userService.createUser("vasya", group);
        User user2 = userService.createUser("vova", group);
        entityService.createEntity(user1, "ent", "test");
        assertNull(entityService.getEntityValue(user2, "ent"));
    }

    @Test
    public void testFailWriteBySameGroup(){
        Group group = userService.createGroup("test", Collections.singletonList(Permission.read));
        User user1 = userService.createUser("vasya", group);
        User user2 = userService.createUser("vova", group);
        entityService.createEntity(user1, "ent", "test");
        assertFalse(entityService.updateEntity(user2, "ent", "asd"));
    }

}
