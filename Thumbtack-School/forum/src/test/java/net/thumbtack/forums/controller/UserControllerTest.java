package net.thumbtack.forums.controller;

import com.google.gson.Gson;
import net.thumbtack.forums.dto.request.user.ChangePassDto;
import net.thumbtack.forums.dto.request.user.RegisterDto;
import net.thumbtack.forums.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final Gson GSON = new Gson();

    @ParameterizedTest
    @ValueSource(strings = {"User", "User123", "Юзер", "Юзер123"})
    void testRegisterUserWithNames(String name) throws Exception {
        RegisterDto registerDto = new RegisterDto(name, "em@em.com", "password123");
        String json = GSON.toJson(registerDto);
        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("JAVASESSIONID"));
        verify(userService, times(1)).registerUser(eq(registerDto), anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"password1234", "pass_!214>", "pàssword1234", "passwordpassword"})
    void testRegisterUserWithPass(String pass) throws Exception {
        RegisterDto registerDto = new RegisterDto("name", "em@em.com", pass);
        String json = GSON.toJson(registerDto);
        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("JAVASESSIONID"));
        verify(userService, times(1)).registerUser(eq(registerDto), anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"email@em.com", "email_@em.com", "R.email_R@em.com", "ruemail@em.ru", "yankdv99@gmail.com"})
    void testRegisterUserWithEmails(String email) throws Exception {
        RegisterDto registerDto = new RegisterDto("name", email, "password123");
        String json = GSON.toJson(registerDto);
        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("JAVASESSIONID"));
        verify(userService, times(1)).registerUser(eq(registerDto), anyString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"_User_", "thisisalongerthan50charactersnameeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", "wièrd"})
    void testFailRegisterUserWrongName(String name) throws Exception {
        RegisterDto registerDto = new RegisterDto(name, "em@em.com", "password123");
        String json = GSON.toJson(registerDto);
        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"short"})
    void testFailRegisterUserWrongPass(String pass) throws Exception {
        RegisterDto registerDto = new RegisterDto("name", "em@em.com", pass);
        String json = GSON.toJson(registerDto);
        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"email", "почта@em.com", "...@em.com", "em@em", "em@e.com?", "em@em..com"})
    void testFailRegisterUserWrongEmail(String email) throws Exception {
        RegisterDto registerDto = new RegisterDto("name", email, "password123");
        String json = GSON.toJson(registerDto);
        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(delete("/api/users")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser("val");
    }

    @Test
    void testChangePassword() throws Exception {
        ChangePassDto changePassDto = new ChangePassDto("dima", "password123", "pass123456");
        String json = GSON.toJson(changePassDto);
        mvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(userService, times(1)).changePassword(eq(changePassDto), eq("val"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"pass", "1"})
    void testFailChangePassWrongNewPass(String pass) throws Exception {
        ChangePassDto changePassDto = new ChangePassDto("dima", "password123", pass);
        String json = GSON.toJson(changePassDto);
        mvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"pass", "1"})
    void testFailChangePasswordWrongOldPass(String pass) throws Exception {
        ChangePassDto changePassDto = new ChangePassDto("dima", pass, "pass123456");
        String json = GSON.toJson(changePassDto);
        mvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @Test
    void testUpdateToSuper() throws Exception {
        mvc.perform(put("/api/users/1/super")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(userService, times(1)).updateToSuper(eq(1L), eq("val"));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void testFailUpdateToSuper(int id) throws Exception {
        mvc.perform(put("/api/users/" + id + "/super")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @Test
    void testBanUser() throws Exception {
        mvc.perform(post("/api/users/1/restrict")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(userService, times(1)).banUser(eq(1L), eq("val"));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void testFailBanUser(int id) throws Exception {
        mvc.perform(post("/api/users/" + id + "/restrict")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @Test
    void testGetUsersInfo() throws Exception {
        mvc.perform(get("/api/users/")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(userService, times(1)).getUsersInfo(eq("val"));
    }

    @Test
    void testFailGetUsersInfoWrongCookie() throws Exception {
        mvc.perform(get("/api/users/")
                .cookie(new Cookie("wrong_cookie", "val")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }
}
