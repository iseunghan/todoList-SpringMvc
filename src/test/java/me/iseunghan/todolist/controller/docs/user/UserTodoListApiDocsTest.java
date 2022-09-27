package me.iseunghan.todolist.controller.docs.user;

import me.iseunghan.todolist.controller.docs.RestDocumentSupport;
import me.iseunghan.todolist.controller.user.UserTodoListApiController;
import me.iseunghan.todolist.exception.NotFoundException;
import me.iseunghan.todolist.exception.model.ErrorCode;
import me.iseunghan.todolist.jwt.JwtAuthorizationFilter;
import me.iseunghan.todolist.jwt.JwtTokenUtil;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.model.dto.CreateTodoItemRequest;
import me.iseunghan.todolist.model.dto.PageDto;
import me.iseunghan.todolist.model.dto.RetrieveTodoItemResponse;
import me.iseunghan.todolist.model.dto.TodoItemDto;
import me.iseunghan.todolist.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
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
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserTodoListApiController.class)
public class UserTodoListApiDocsTest extends RestDocumentSupport {

    @MockBean
    private TodoService todoService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @WithMockUser
    void Create_Todo() throws Exception {
        // given
        CreateTodoItemRequest dto = CreateTodoItemRequest.builder()
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
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn("user");

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
                                        fieldWithPath("title").description("할일 내용").attributes(key("required").value(true))
                                ),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부 (true/false)"),
                                        fieldWithPath("content.id").description("할일의 아이디 값"),
                                        fieldWithPath("content.title").description("할일 내용"),
                                        fieldWithPath("content.createdAt").description("생성 날짜"),
                                        fieldWithPath("content.updatedAt").description("수정 날짜"),
                                        fieldWithPath("content.username").description("사용자 아이디"),
                                        fieldWithPath("content.status").description("할일의 상태 값")
                                )
                        )
                )
                .andExpect(status().isCreated())
        ;
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void Create_Todo_400() throws Exception {
        // given
        CreateTodoItemRequest dto = CreateTodoItemRequest.builder()
                .title("")
                .build();

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
                                        fieldWithPath("title").description("할일 내용").attributes(key("required").value(true))
                                ),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부 (true/false)"),
                                        fieldWithPath("error.code").description("에러 코드"),
                                        fieldWithPath("error.message").description("에러 메세지"),
                                        fieldWithPath("error.fieldErrors[].field").description("필드명"),
                                        fieldWithPath("error.fieldErrors[].value").description("사용자가 입력한 값"),
                                        fieldWithPath("error.fieldErrors[].reason").description("필드에 대한 에러 메세지")
                                )
                        )
                )
                .andExpect(status().isBadRequest())
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
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn("user");

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
                                        fieldWithPath("success").description("성공 여부 (true/false)"),
                                        fieldWithPath("content.todoList[0].id").description("할일의 idx"),
                                        fieldWithPath("content.todoList[0].title").description("할일 내용"),
                                        fieldWithPath("content.todoList[0].createdAt").description("생성 날짜"),
                                        fieldWithPath("content.todoList[0].updatedAt").description("수정 날짜"),
                                        fieldWithPath("content.todoList[0].username").description("사용자 아이디"),
                                        fieldWithPath("content.todoList[0].status").description("할일의 상태 값"),
                                        fieldWithPath("content.pageable.number").description("현재 페이지 번호"),
                                        fieldWithPath("content.pageable.totalElements").description("전체 할일 개수"),
                                        fieldWithPath("content.pageable.totalPages").description("전체 페이지 수"),
                                        fieldWithPath("content.pageable.first").description("현재 첫번째 페이지 여부"),
                                        fieldWithPath("content.pageable.last").description("현재 마지막 페이지 여부")
                                )
                        )
                )
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

        given(todoService.findById(anyLong())).willReturn(dto);
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn("user");

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
                                        fieldWithPath("success").description("성공 여부 (true/false)"),
                                        fieldWithPath("content.id").description("할일의 idx"),
                                        fieldWithPath("content.title").description("할일 내용"),
                                        fieldWithPath("content.createdAt").description("생성 날짜"),
                                        fieldWithPath("content.updatedAt").description("수정 날짜"),
                                        fieldWithPath("content.username").description("사용자 아이디"),
                                        fieldWithPath("content.status").description("할일의 상태 값")
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void Retrieve_Todo_403() throws Exception {
        // given

        // when & then
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
                                        fieldWithPath("success").description("성공 여부 (true/false)"),
                                        fieldWithPath("error.code").description("에러 코드"),
                                        fieldWithPath("error.message").description("에러 메세지")
                                )
                        )
                )
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void Retrieve_Todo_404() throws Exception {
        // given
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn("user");
        given(todoService.findById(anyLong())).willThrow(new NotFoundException(ErrorCode.NOT_FOUND_TODO));

        // when & then
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
                                        fieldWithPath("success").description("성공 여부 (true/false)"),
                                        fieldWithPath("error.code").description("에러 코드"),
                                        fieldWithPath("error.message").description("에러 메세지")
                                )
                        )
                )
                .andExpect(status().isNotFound())
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
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn("user");

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
                                        fieldWithPath("success").description("성공 여부 (true/false)"),
                                        fieldWithPath("content.id").description("할일의 아이디 값"),
                                        fieldWithPath("content.title").description("할일 내용"),
                                        fieldWithPath("content.createdAt").description("생성 날짜"),
                                        fieldWithPath("content.updatedAt").description("수정 날짜"),
                                        fieldWithPath("content.username").description("사용자 아이디"),
                                        fieldWithPath("content.status").description("할일의 상태 값")
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
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn("user");

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
                                        fieldWithPath("success").description("성공 여부 (true/false)")
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }
}
