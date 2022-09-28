package me.iseunghan.todolist.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminTodoListApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void ADMIN은_모든_할일을_조회할_수있다() throws Exception {
        mockMvc.perform(get("/admin/accounts/todolist")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content.todoList[0].id").exists())
                .andExpect(jsonPath("content.todoList[0].username").exists())
                .andExpect(jsonPath("content.pageable").exists())
        ;
    }
}