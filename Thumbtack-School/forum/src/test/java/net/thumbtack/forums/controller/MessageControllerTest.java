package net.thumbtack.forums.controller;


import com.google.gson.Gson;
import net.thumbtack.forums.dto.response.information.MessageCountDto;
import net.thumbtack.forums.model.enums.OrderValue;
import net.thumbtack.forums.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @MockBean
    private MessageService messageService;

    @Autowired
    private MockMvc mvc;

    private final Gson GSON = new Gson();

    @Test
    void testGetForumMessages() throws Exception {
        mvc.perform(get("/api/forums/1/messages?allversions=true&nocomments=true&unpublished=true" +
                "&tags=tag1&tags=tag2&order=ASC&offset=2&limit=100")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(messageService, times(1)).getAllMessagesInfo(
                eq(1),
                eq(true),
                eq(true),
                eq(true),
                eq(Arrays.asList("tag1", "tag2")),
                eq(OrderValue.ASC),
                eq(2L),
                eq(100),
                eq("val")
        );
    }

    @Test
    void testGetForumMessagesNoParams() throws Exception {
        mvc.perform(get("/api/forums/1/messages")
                .cookie(new Cookie("JAVASESSIONID", "val")))
                .andExpect(status().isOk());
        verify(messageService, times(1)).getAllMessagesInfo(
                eq(1),
                eq(false),
                eq(false),
                eq(false),
                eq(null),
                eq(OrderValue.DESC),
                eq(0L),
                eq(1000),
                eq("val")
        );
    }
}
