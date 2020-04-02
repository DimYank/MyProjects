package net.thumbtack.forums.controller;

import com.google.gson.Gson;
import net.thumbtack.forums.dto.request.forum.CreateForumDto;
import net.thumbtack.forums.model.enums.ForumType;
import net.thumbtack.forums.service.ForumService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ForumController.class)
public class ForumControllerTest {

    @MockBean
    private ForumService forumService;

    @Autowired
    private MockMvc mvc;

    private final Gson GSON = new Gson();

    @Test
    void testCreateForum() throws Exception {
        CreateForumDto createForumDto = new CreateForumDto("name", ForumType.MODERATED);
        String json = GSON.toJson(createForumDto);
        mvc.perform(post("/api/forums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(forumService, times(1)).createForum(eq(createForumDto), eq("val"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"_Forum_", "thisisalongerthan50charactersnameeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", "wièrd"})
    void testFailCreateForum(String name) throws Exception {
        CreateForumDto createForumDto = new CreateForumDto(name, null);
        String json = GSON.toJson(createForumDto);
        mvc.perform(post("/api/forums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    @Test
    void testDeleteForum() throws Exception {
        mvc.perform(delete("/api/forums/1")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(forumService, times(1)).deleteForum(eq(1), eq("val"));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void testFailDeleteForum(int id) throws Exception {
        mvc.perform(delete("/api/forums/" + id)
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @Test
    void testGetForumInfo() throws Exception {
        mvc.perform(get("/api/forums/1")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(forumService, times(1)).getForumById(eq(1), eq("val"));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void testFaiGetForumInfo(int id) throws Exception {
        mvc.perform(get("/api/forums/" + id)
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @Test
    void testGetUsersInfo() throws Exception {
        mvc.perform(get("/api/forums/")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(forumService, times(1)).getAllForums(eq("val"));
    }

    @Test
    void testFailGetUsersInfoWrongCookie() throws Exception {
        mvc.perform(get("/api/forums/")
                .cookie(new Cookie("wrong_cookie", "val")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }
}
