package me.iseunghan.todolist.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.iseunghan.todolist.jwt.JwtTokenUtil;
import me.iseunghan.todolist.model.dto.CreateAccountRequest;
import me.iseunghan.todolist.model.dto.CreateTodoItemRequest;
import me.iseunghan.todolist.model.dto.TodoItemDto;
import me.iseunghan.todolist.service.AccountService;
import me.iseunghan.todolist.service.TodoService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTodoListApiControllerTest {

    protected static final String TOKEN = "Bearer 12345";
    private static final String USERNAME = "testuser";
    List<TodoItemDto> testTodoDtos;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TodoService todoService;
    @Autowired
    private AccountService accountService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @BeforeAll
    void setUp() {
        accountService.addAccount(CreateAccountRequest.builder()
                .username(USERNAME)
                .email("teest@email.com")
                .password("1234")
                .nickname("nick")
                .build());
        testTodoDtos = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            TodoItemDto todo = todoService.addTodo(USERNAME, CreateTodoItemRequest.builder()
                    .title("test" + i)
                    .build());

            testTodoDtos.add(todo);
        }
    }

    @Test
    @WithMockUser
    void 할일을_추가_할수있다() throws Exception {
        // given
        CreateTodoItemRequest dto = CreateTodoItemRequest.builder()
                .title("title")
                .build();
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn(USERNAME);

        // when
        mockMvc.perform(post("/user/accounts/{username}/todolist", USERNAME)
                        .header("Authorization", TOKEN)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("content.id").exists())
                .andExpect(jsonPath("content.title").exists())
        ;
    }

    @Test
    @WithMockUser
    void 할일을_추가_할수있다_title이_비어있으면_400() throws Exception {
        // given
        CreateTodoItemRequest request = CreateTodoItemRequest.builder()
                .title("")
                .build();
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn(USERNAME);

        // when
        mockMvc.perform(post("/user/accounts/{username}/todolist", USERNAME)
                        .header("Authorization", TOKEN)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("error.code").exists())
                .andExpect(jsonPath("error.message").exists())
                .andExpect(jsonPath("error.fieldErrors").exists())
        ;
    }

    @Test
    @WithMockUser
    void 할일을_조회_할수있다() throws Exception {
        // given
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn(USERNAME);

        // when
        mockMvc.perform(get("/user/accounts/{username}/todolist", USERNAME)
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("content.todoList").exists())
                .andExpect(jsonPath("content.pageable").exists())
        ;
    }

    @Test
    @WithMockUser
    void 하나의_할일을_상세조회_할수있다() throws Exception {
        // given
        TodoItemDto todoItem = testTodoDtos.get(0);
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn(USERNAME);

        // when
        mockMvc.perform(get("/user/accounts/{username}/todolist/{id}", USERNAME, todoItem.getId())
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("content.id").value(todoItem.getId()))
                .andExpect(jsonPath("content.title").exists())
        ;
    }

    @Test
    @WithMockUser
    void 할일의_상태를_변경할수_있다() throws Exception {
        // given
        TodoItemDto todoItem = testTodoDtos.get(1);
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn(USERNAME);

        // when
        mockMvc.perform(patch("/user/accounts/{username}/todolist/{id}", USERNAME, todoItem.getId())
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("content.id").exists())
        ;
    }

    @Test
    @WithMockUser
    void 하나의_할일을_삭제할수_있다_200() throws Exception {
        // given
        TodoItemDto todoItem = testTodoDtos.get(4);
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn(USERNAME);

        // when
        mockMvc.perform(patch("/user/accounts/{username}/todolist/{id}", USERNAME, todoItem.getId())
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("content.id").exists())
        ;
    }

    @Test
    @WithMockUser
    void 하나의_할일을_삭제할때_존재하지_않는다면_404() throws Exception {
        // given
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn(USERNAME);

        // when
        mockMvc.perform(patch("/user/accounts/{username}/todolist/{id}", USERNAME, 1000)
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error").exists())
                .andExpect(jsonPath("error.code").exists())
        ;
    }

}