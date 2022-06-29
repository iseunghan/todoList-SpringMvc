package me.iseunghan.todolist.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.iseunghan.todolist.model.dto.TodoItemDto;
import me.iseunghan.todolist.service.TodoService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTodoListApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoService todoService;

    List<TodoItemDto> testTodoDtos;

    @BeforeAll
    void setup() {
        // given
        testTodoDtos = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            TodoItemDto todo = todoService.addTodo("user", TodoItemDto.builder()
                    .title("test" + i)
                    .build());

            testTodoDtos.add(todo);
        }
    }

    @Test
    @WithMockUser(username = "user")
    void 할일을_추가_할수있다() throws Exception {
        // given
        TodoItemDto dto = TodoItemDto.builder()
                .title("title")
                .build();

        // when
        mockMvc.perform(post("/user/accounts/{username}/todolist", "user")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").exists())
        ;
    }

    @Test
    @WithMockUser
    void 할일을_조회_할수있다() throws Exception {

        // when
        mockMvc.perform(get("/user/accounts/{username}/todolist", "user"))
                .andDo(print())
                .andExpect(jsonPath("todoList[0].username").exists())
                .andExpect(jsonPath("pageable").exists())
        ;
    }

    @Test
    @WithMockUser
    void 하나의_할일을_상세조회_할수있다() throws Exception {
        // given
        TodoItemDto todoItem = testTodoDtos.get(0);

        // when
        mockMvc.perform(get("/user/accounts/{username}/todolist/{id}", "user", todoItem.getId()))
                .andDo(print())
                .andExpect(jsonPath("id").value(todoItem.getId()))
                .andExpect(jsonPath("title").exists())
        ;
    }

}