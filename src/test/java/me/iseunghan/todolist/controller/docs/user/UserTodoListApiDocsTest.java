package me.iseunghan.todolist.controller.docs.user;

import me.iseunghan.todolist.config.SecurityConfig;
import me.iseunghan.todolist.controller.docs.RestDocumentSupport;
import me.iseunghan.todolist.controller.user.UserTodoListApiController;
import me.iseunghan.todolist.jwt.JwtTokenUtil;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.model.dto.PageDto;
import me.iseunghan.todolist.model.dto.RetrieveTodoItemResponse;
import me.iseunghan.todolist.model.dto.TodoItemDto;
import me.iseunghan.todolist.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserTodoListApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )}
)
public class UserTodoListApiDocsTest extends RestDocumentSupport {

    @MockBean
    private TodoService todoService;

    @MockBean
    private JwtTokenUtil tokenUtil;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void Create_Todo() throws Exception {
        // given
        TodoItemDto dto = TodoItemDto.builder()
                .title("title")
                .build();
        TodoItemDto response = TodoItemDto.builder()
                .id(5L)
                .title("댕댕이 산책시키기")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .username("John")
                .status(TodoStatus.NEVER)
                .build();
        given(todoService.addTodo(anyString(), any())).willReturn(response);
        given(tokenUtil.isCorrectUsername(anyString(), anyString())).willReturn(true);

        // when
        mockMvc.perform(post("/user/accounts/{username}/todolist", "user")
                        .header("Authorization", TOKEN)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰 값")
                                ),
                                pathParameters(
                                        parameterWithName("username").description("사용자 아이디")
                                ),
                                requestFields(
                                        fieldWithPath("title").description("할일 내용"),
                                        fieldWithPath("id").ignored(),
                                        fieldWithPath("username").ignored(),
                                        fieldWithPath("createdAt").ignored(),
                                        fieldWithPath("updatedAt").ignored(),
                                        fieldWithPath("status").ignored()
                                ),
                                responseFields(
                                        fieldWithPath("id").description("할일의 아이디 값"),
                                        fieldWithPath("title").description("할일 내용"),
                                        fieldWithPath("createdAt").description("생성 날짜"),
                                        fieldWithPath("updatedAt").description("수정 날짜"),
                                        fieldWithPath("username").description("사용자 아이디"),
                                        fieldWithPath("status").description("할일의 상태 값")
                                )
                        )
                )
                .andExpect(status().isCreated())
        ;
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void Retrieve_TodoList() throws Exception {
        // given
        TodoItemDto dto = TodoItemDto.builder()
                .id(5L)
                .title("댕댕이 산책시키기")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .username("John")
                .status(TodoStatus.DONE)
                .build();
        TodoItemDto dto1 = TodoItemDto.builder()
                .id(6L)
                .title("저녁 식사 하기")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .username("John")
                .status(TodoStatus.NEVER)
                .build();
        RetrieveTodoItemResponse response = RetrieveTodoItemResponse.builder()
                .todoList(List.of(dto, dto1))
                .pageable(PageDto.builder()
                        .first(true)
                        .last(true)
                        .number(0)
                        .totalElements(2)
                        .totalPages(1)
                        .build())
                .build();
        given(todoService.findUserTodoList(any(), anyString())).willReturn(response);
        given(tokenUtil.isCorrectUsername(anyString(), anyString())).willReturn(true);

        // when
        mockMvc.perform(get("/user/accounts/{username}/todolist", "user")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰 값")
                                ),
                                pathParameters(
                                        parameterWithName("username").description("사용자 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("todoList[0].id").description("할일의 아이디 값"),
                                        fieldWithPath("todoList[0].title").description("할일 내용"),
                                        fieldWithPath("todoList[0].createdAt").description("생성 날짜"),
                                        fieldWithPath("todoList[0].updatedAt").description("수정 날짜"),
                                        fieldWithPath("todoList[0].username").description("사용자 아이디"),
                                        fieldWithPath("todoList[0].status").description("할일의 상태 값"),
                                        fieldWithPath("pageable.number").description("현재 페이지 번호"),
                                        fieldWithPath("pageable.totalElements").description("전체 할일 개수"),
                                        fieldWithPath("pageable.totalPages").description("전체 페이지 수"),
                                        fieldWithPath("pageable.first").description("현재 첫번째 페이지 여부"),
                                        fieldWithPath("pageable.last").description("현재 마지막 페이지 여부")
                                )
                        )
                )
                .andExpect(jsonPath("todoList[0].username").exists())
                .andExpect(jsonPath("pageable").exists())
        ;
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void Retrieve_Todo() throws Exception {
        // given
        TodoItemDto dto = TodoItemDto.builder()
                .id(5L)
                .title("댕댕이 산책시키기")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .username("John")
                .status(TodoStatus.DONE)
                .build();
        RetrieveTodoItemResponse response = RetrieveTodoItemResponse.builder()
                .todoList(List.of(dto))
                .pageable(PageDto.builder()
                        .first(true)
                        .last(true)
                        .number(0)
                        .totalElements(1)
                        .totalPages(1)
                        .build())
                .build();
        given(todoService.findById(anyLong())).willReturn(dto);
        given(tokenUtil.isCorrectUsername(anyString(), anyString())).willReturn(true);

        // when
        mockMvc.perform(get("/user/accounts/{username}/todolist/{todo_id}", "user", 5)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰 값")
                                ),
                                pathParameters(
                                        parameterWithName("username").description("사용자 아이디"),
                                        parameterWithName("todo_id").description("할일 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("할일의 아이디 값"),
                                        fieldWithPath("title").description("할일 내용"),
                                        fieldWithPath("createdAt").description("생성 날짜"),
                                        fieldWithPath("updatedAt").description("수정 날짜"),
                                        fieldWithPath("username").description("사용자 아이디"),
                                        fieldWithPath("status").description("할일의 상태 값")
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateTodo() throws Exception {
        // given
        TodoItemDto dto = TodoItemDto.builder()
                .id(7L)
                .title("댕댕이 목욕시키기")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .username("John")
                .status(TodoStatus.DONE)
                .build();

        given(todoService.updateStatus(anyLong())).willReturn(dto);
        given(tokenUtil.isCorrectUsername(anyString(), anyString())).willReturn(true);

        // when & then
        mockMvc.perform(patch("/user/accounts/{username}/todolist/{todo_id}", "user", 7)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰 값")
                                ),
                                pathParameters(
                                        parameterWithName("username").description("사용자 아이디"),
                                        parameterWithName("todo_id").description("할일 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("할일의 아이디 값"),
                                        fieldWithPath("title").description("할일 내용"),
                                        fieldWithPath("createdAt").description("생성 날짜"),
                                        fieldWithPath("updatedAt").description("수정 날짜"),
                                        fieldWithPath("username").description("사용자 아이디"),
                                        fieldWithPath("status").description("할일의 상태 값")
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleteTodo() throws Exception {
        // given
        given(todoService.deleteTodoItem(anyLong())).willReturn(8L);
        given(tokenUtil.isCorrectUsername(anyString(), anyString())).willReturn(true);

        // when & then
        mockMvc.perform(delete("/user/accounts/{username}/todolist/{todo_id}", "user", 7)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰 값")
                                ),
                                pathParameters(
                                        parameterWithName("username").description("사용자 아이디"),
                                        parameterWithName("todo_id").description("할일 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("삭제된 할일의 아이디 값")
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }
}
