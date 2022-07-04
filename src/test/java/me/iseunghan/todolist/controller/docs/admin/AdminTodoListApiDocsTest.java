package me.iseunghan.todolist.controller.docs.admin;

import me.iseunghan.todolist.config.SecurityConfig;
import me.iseunghan.todolist.controller.admin.AdminTodoListApiController;
import me.iseunghan.todolist.controller.docs.RestDocumentSupport;
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
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminTodoListApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )}
)
public class AdminTodoListApiDocsTest extends RestDocumentSupport {

    @MockBean
    private TodoService todoService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void Retrieve_All_TodoList() throws Exception {
        // given
        TodoItemDto dto1 = TodoItemDto.builder()
                .id(1L)
                .title("댕댕이 산책시키기")
                .status(TodoStatus.DONE)
                .username("john1234")
                .build();
        TodoItemDto dto2 = TodoItemDto.builder()
                .id(2L)
                .title("댕댕이 목욕시키기")
                .status(TodoStatus.NEVER)
                .username("john1234")
                .build();
        RetrieveTodoItemResponse response = RetrieveTodoItemResponse.builder()
                .todoList(List.of(dto1, dto2))
                .pageable(PageDto.builder()
                        .number(0)
                        .totalElements(2)
                        .totalPages(1)
                        .first(true)
                        .last(true)
                        .build())
                .build();
        given(todoService.findAll(any())).willReturn(response);

        // when
        mockMvc.perform(get("/admin/accounts/todolist")
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰 값")
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
                .andExpect(status().isOk());
    }
}
