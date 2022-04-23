package me.iseunghan.todolist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.iseunghan.todolist.assembler.TodoResourceAssembler;
import me.iseunghan.todolist.exception.NotEmptyException;
import me.iseunghan.todolist.exception.NotFoundException;
import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoItemDto;
import me.iseunghan.todolist.model.TodoResource;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TodoListController.class)
class TodoListControllerMockTest {

    @MockBean
    private TodoService todoService;

    @MockBean
    private TodoResourceAssembler todoResourceAssembler;

    @MockBean
    private PagedResourcesAssembler<TodoItem> assembler;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    private List<TodoItem> todoList = new ArrayList<>();

    @BeforeEach
    void getMockList() {
        for (int i = 1; i < 5; i++) {
            this.todoList.add(TodoItem.builder()
                    .id((long) i)
                    .title("test" + i)
                    .status(TodoStatus.NEVER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build()
            );
        }
    }

    @Test
    void 할일을_추가할_수있다() throws Exception {
        // given
        TodoItem todo = this.todoList.get(0);
        TodoItemDto todoDto = modelMapper.map(todo, TodoItemDto.class);

        given(todoService.addTodo(any(TodoItemDto.class))).willReturn(todo);

        // when
        mockMvc.perform(post("/todoList")
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id", is(todoDto.getId().intValue())))
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.put").exists())
                .andExpect(jsonPath("_links.delete").exists())
        ;
    }

    @Test
    void title_공백으로_저장하면_400() throws Exception {
        // given
        TodoItemDto todoDto = TodoItemDto.builder().title("").build();
        given(todoService.addTodo(any(TodoItemDto.class))).willThrow(NotEmptyException.class);

        // when
        mockMvc.perform(post("/todoList")
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    void 하나의_할일을_조회할_수있다() throws Exception {
        // given
        TodoItem todo = this.todoList.get(0);

        given(todoService.findById(anyLong())).willReturn(todo);

        // when
        mockMvc.perform(get("/todoList/" + todo.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(todo.getId().intValue())))
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("updatedAt").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.put").exists())
                .andExpect(jsonPath("_links.delete").exists())
        ;
    }

    @Test
    void 없는_할일을_조회하면_NoSuchElementsException() throws Exception {
        // given
        TodoItem todo = this.todoList.get(0);

        given(todoService.findById(anyLong())).willThrow(NotFoundException.class);

        // when
        mockMvc.perform(get("/todoList/" + todo.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    void 할일을_업데이트할_수있다() throws Exception {
        // given
        TodoItem todo = this.todoList.get(0);
        TodoItem updateTodo = modelMapper.map(todo, TodoItem.class);
        updateTodo.setStatus(TodoStatus.DONE);
        updateTodo.setUpdatedAt(LocalDateTime.now());

        given(todoService.updateStatus(anyLong())).willReturn(updateTodo);

        // when
        mockMvc.perform(patch("/todoList/" + todo.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(todo.getId().intValue())))
                .andExpect(jsonPath("status", is(todo.getStatus().toString())))
                .andExpect(jsonPath("updatedAt", not(todo.getUpdatedAt())))
        ;
    }

    @Test
    void 없는_할일을_수정하면_NoSuchElementsException() throws Exception {
        // given
        given(todoService.updateStatus(anyLong())).willThrow(NotFoundException.class);

        // when
        mockMvc.perform(patch("/todoList/100")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    void 할일을_삭제할_수있다() throws Exception {
        // given
        given(todoService.deleteTodoItem(123L)).willReturn(123L);

        // when
        mockMvc.perform(delete("/todoList/123"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    void 없는_할일을_삭제하면_404() throws Exception {
        // given
        given(todoService.deleteTodoItem(anyLong())).willThrow(NotFoundException.class);

        // when
        mockMvc.perform(delete("/todoList/" + anyLong()))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }
}