package com.lineate.traineeship;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class TestEntityRepository {

    private ServiceFactory serviceFactory = new ServiceFactory();
    private UserService userService = serviceFactory.createUserService();

    @Test
    public void testSaveEntity(){
        EntityRepository entityRepository = mock(EntityRepository.class);
        EntityService entityService = serviceFactory.createEntityService(entityRepository);
        Group group = userService.createGroup("test", Arrays.asList(Permission.read, Permission.write));
        User user = userService.createUser("vasya", group);
        entityService.createEntity(user, "ent", "enttest");
        verify(entityRepository, times(1)).save(any(Entity.class));
    }

    @Test
    public void testFailSaveEntity(){
        EntityRepository entityRepository = mock(EntityRepository.class);
        EntityService entityService = serviceFactory.createEntityService(entityRepository);
        Group group = userService.createGroup("test", Arrays.asList(Permission.read, Permission.write));
        User user = userService.createUser("vasya", group);
        entityService.createEntity(user, "", "enttest");
        verify(entityRepository, never()).save(any(Entity.class));
    }

    @Test
    public void testGetEntity(){
        EntityRepository entityRepository = mock(EntityRepository.class);
        EntityService entityService = serviceFactory.createEntityService(entityRepository);
        when(entityRepository.get("ent")).thenReturn(null);
        Group group = userService.createGroup("test", Arrays.asList(Permission.read, Permission.write));
        User user = userService.createUser("vasya", group);
        entityService.createEntity(user, "ent", "enttest");
        entityService.getEntityValue(user, "ent");
        verify(entityRepository, times(1)).get("ent");
    }

    @Test
    public void testFailGetEntity(){
        EntityRepository entityRepository = mock(EntityRepository.class);
        EntityService entityService = serviceFactory.createEntityService(entityRepository);
        when(entityRepository.get("ent")).thenReturn(null);
        entityService.getEntityValue(null, "ent");
        verify(entityRepository, never()).get("ent");
    }
}
