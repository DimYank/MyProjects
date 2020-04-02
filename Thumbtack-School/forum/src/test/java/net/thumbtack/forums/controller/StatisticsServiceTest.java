package net.thumbtack.forums.controller;

import com.google.gson.Gson;
import net.thumbtack.forums.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(net.thumbtack.forums.controller.StatisticsController.class)
public class StatisticsServiceTest {

    @MockBean
    private StatisticsService informationService;

    @Autowired
    private MockMvc mvc;

    private final Gson GSON = new Gson();

    @Test
    void testGetSettings() throws Exception {
        mvc.perform(get("/api/settings")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(informationService, times(1)).getServerSettings(eq("val"));
    }

    @Test
    void testGetSettingsNoCookie() throws Exception {
        mvc.perform(get("/api/settings"))
                .andExpect(status().isOk());
        verify(informationService, times(1)).getServerSettings(null);
    }

    @Test
    void testGetMessageAndCommentCount() throws Exception {
        mvc.perform(get("/api/stats/messages?forumId=1")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(informationService, times(1)).getMessageAndCommentCount(eq(1), eq("val"));
    }

    @Test
    void testGetMessageAndCommentCountNoForumId() throws Exception {
        mvc.perform(get("/api/stats/messages")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(informationService, times(1)).getMessageAndCommentCount(eq(0), eq("val"));
    }

    @Test
    void testFailGetMessageAndCommentCount() throws Exception {
        mvc.perform(get("/api/stats/messages?forumId=asd")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @Test
    void testGetMessagesRating() throws Exception {
        mvc.perform(get("/api/stats/messages/rating?forumId=1&forMessages=false&offset=2&limit=100")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(informationService, times(1)).getRating(eq(1), eq(false),
                eq(2L), eq(100),  eq("val"));
    }

    @Test
    void testGetMessagesRatingNoParams() throws Exception {
        mvc.perform(get("/api/stats/messages/rating")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(informationService, times(1)).getRating(eq(0), eq(true),
                eq(0L), eq(1000),  eq("val"));
    }

    @Test
    void testFailGetMessagesRating() throws Exception {
        mvc.perform(get("/api/stats/messages/rating?forumId=0&forMessages=2&offset=-1&limit=0")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }

    @Test
    void testGetUsersRating() throws Exception {
        mvc.perform(get("/api/stats/users/rating?forumId=1&offset=2&limit=100")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(informationService, times(1)).getUsersRating(eq(1), eq(2L),
                eq(100), eq("val"));
    }

    @Test
    void testGetUsersRatingNoParams() throws Exception {
        mvc.perform(get("/api/stats/users/rating")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(informationService, times(1)).getUsersRating(eq(0), eq(0L),
                eq(1000),  eq("val"));
    }

    @Test
    void testFailGetUsersRating() throws Exception {
        mvc.perform(get("/api/stats/users/rating?forumId=0&offset=-1&limit=0")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors", hasSize(1)));
    }
}
