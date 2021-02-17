// new Controller Test Source code

package me.iseunghan.todolist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.iseunghan.todolist.model.TodoItem;
import me.iseunghan.todolist.model.TodoStatus;
import me.iseunghan.todolist.model.TodoitemDto;
import me.iseunghan.todolist.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)  // junit4 @RunWith(SpringRunner.class)와 동일하다.
@SpringBootTest
@AutoConfigureMockMvc
class TodoListControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TodoRepository todoRepository;
    @Autowired
    WebApplicationContext ctx;

    /**
     * mokMvc 한글 깨짐 현상은 MockMvc를 설정할 때 CharacterEncodingFilter를 추가 하면 해결할 수 있습니다.
     */
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("GET \"/todoLists/{id}\" 하나의 할일 조회")
    public void selectOne() throws Exception {
        // given
        TodoItem todoItem = new TodoItem();
        todoItem.setTitle("select one todoItem");
        todoItem.setStatus(TodoStatus.NEVER);
        todoItem.setDate(LocalDate.now());

        todoRepository.save(todoItem);

        // when & then
        mockMvc.perform(get("/todoLists/{id}", todoItem.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("date").exists())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.put").exists())
                .andExpect(jsonPath("_links.delete").exists())
        ;
    }

    @Test
    @DisplayName("GET \"/todoLists\" 전체 할일 조회")
    public void selectAll() throws Exception {
        // given
        TodoItem item = new TodoItem();
        item.setTitle("create TodoListControllerTest1");
        item.setStatus(TodoStatus.NEVER);
        item.setDate(LocalDate.now());

        TodoItem item2 = new TodoItem();
        item2.setTitle("create TodoListControllerTest2");
        item2.setStatus(TodoStatus.NEVER);
        item2.setDate(LocalDate.now());

        TodoItem item3 = new TodoItem();
        item3.setTitle("create TodoListControllerTest3");
        item3.setStatus(TodoStatus.NEVER);
        item3.setDate(LocalDate.now());

        todoRepository.save(item);
        todoRepository.save(item2);
        todoRepository.save(item3);

        // when & then
        mockMvc.perform(get("/todoLists"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.todoResourceList[0].id").exists())
                .andExpect(jsonPath("_embedded.todoResourceList[0].title").exists())
                .andExpect(jsonPath("_embedded.todoResourceList[0].status").exists())
                .andExpect(jsonPath("_embedded.todoResourceList[0].date").exists())
                .andExpect(jsonPath("_embedded.todoResourceList[0]._links.self").exists())
                .andExpect(jsonPath("_embedded.todoResourceList[0]._links.put").exists())
                .andExpect(jsonPath("_embedded.todoResourceList[0]._links.delete").exists())
        ;
    }

    @Test
    @DisplayName("GET \"/todoLists/{id}\" 없는 할일 조회")
    public void selectNotFound() throws Exception {

        // when & then
        mockMvc.perform(get("/todoLists/{id}", 5L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST \"/todoLists\" 할일 추가 기능")
    public void createTodo() throws Exception {
        // given
        TodoitemDto todoItem = new TodoitemDto();
        todoItem.setTitle("할일을 추가합니다 Todo");
        todoItem.setDate(LocalDate.now());
        // status는 기본 값이 NEVER

        // when & then
        mockMvc.perform(post("/todoLists")
                .contentType(MediaType.APPLICATION_JSON) // 난 요청 본문에 json을 담아서 보내고 있다.
                .accept(MediaTypes.HAL_JSON)    // 난 HAL_JSON을 받기를 원한다.
                .content(objectMapper.writeValueAsString(todoItem))) // 본문에 objectMapper로 객체를 문자열로 변환해서 넣어준다.
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("status").exists())
                .andExpect(jsonPath("date").exists())
                .andExpect(jsonPath("_links.self").exists())
        ;
    }

    @Test
    @DisplayName("PATCH \"/todoLists/{id}\" 할일 상태 수정")
    public void updateStatus() throws Exception {
        // given
        TodoItem todoItem = new TodoItem();
        todoItem.setTitle("update Todo Status");
        todoItem.setStatus(TodoStatus.NEVER);
        todoItem.setDate(LocalDate.now());
        todoRepository.save(todoItem);

        // when & then
        mockMvc.perform(patch("/todoLists/{id}", todoItem.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("DONE"))
                .andExpect(jsonPath("_links.self").exists())
        ;
    }

    @Test
    @DisplayName("DELETE \"/todoLists/{id}\" 할일 삭제 기능")
    public void deleteTodo() throws Exception {
        // given
        TodoItem todoItem = new TodoItem();
        todoItem.setTitle("delete TodoItem");
        todoItem.setStatus(TodoStatus.NEVER);
        todoItem.setDate(LocalDate.now());
        todoRepository.save(todoItem);

        // when & then
        mockMvc.perform(delete("/todoLists/{id}", todoItem.getId()))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }
}