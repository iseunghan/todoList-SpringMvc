package me.iseunghan.todolist.service;

import me.iseunghan.todolist.exception.NotFoundException;
import me.iseunghan.todolist.exception.model.ErrorCode;
import me.iseunghan.todolist.model.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TodoService todoService;

    @BeforeAll
    void setup() {
        CreateAccountRequest request = CreateAccountRequest.builder()
                .username("test")
                .email("test@email.com")
                .password("pass")
                .nickname("test-nick")
                .build();

        CreateAccountRequest request2 = CreateAccountRequest.builder()
                .username("test2")
                .email("test2@email.com")
                .password("pass")
                .nickname("test2-nick")
                .build();

        accountService.addAccount(request);
        accountService.addAccount(request2);

        for (int i = 0; i < 10; i++) {
            CreateTodoItemRequest todo = CreateTodoItemRequest.builder()
                    .title("title" + i)
                    .build();

            todoService.addTodo(request.getUsername(), todo);
        }
    }

    @Test
    void 회원을_저장할_수있다() {
        // given
        CreateAccountRequest request = CreateAccountRequest.builder()
                .username("testUser")
                .email("testUser@email.com")
                .password("password")
                .nickname("nickname")
                .build();

        // when
        CreateAccountResponse response = accountService.addAccount(request);

        // then
        assertEquals(response.getUsername(), request.getUsername());
        assertEquals(response.getEmail(), request.getEmail());
    }

    @Test
    void ADMIN은_모든_회원_정보를_조회할_수있다() {
        // when
        RetrieveAccountResponse<AdminAccountDto> response = accountService.findAll_ADMIN(PageRequest.of(0, 5));

        // then
        assertEquals(response.getAccountList().get(3).getUsername(), "test");
        assertEquals(response.getAccountList().get(3).getEmail(), "test@email.com");
        assertEquals(response.getAccountList().get(3).getNickname(), "test-nick");
    }

    @Test
    void USER는_모든_회원의_public_정보를_조회할_수있다() {
        // when
        RetrieveAccountResponse<PublicAccountDto> response = accountService.findAll_USER(PageRequest.of(0, 5));
        PublicAccountDto testAccountDto = response.getAccountList().get(3);

        // then
        assertEquals(testAccountDto.getUsername(), "test");
        assertEquals(testAccountDto.getEmail(), "test@email.com");
        assertEquals(testAccountDto.getNickname(), "test-nick");

    }

    @Test
    void ADMIN은_하나의_account_조회할_수있다() {
        // when
        AdminAccountDto account = accountService.findAccount_ADMIN("test");

        // then
        assertEquals(account.getUsername(), "test");
        assertEquals(account.getEmail(), "test@email.com");
        assertEquals(account.getNickname(), "test-nick");
    }

    @Test
    void USER는_자신의_account_조회할_수있다() {
        // when
        RetrieveMyAccountResponse account = accountService.findMyAccount("test");

        // then
        assertEquals(account.getUsername(), "test");
        assertEquals(account.getEmail(), "test@email.com");
        assertEquals(account.getNickname(), "test-nick");
    }

    @Test
    void USER는_하나의_public_account_조회할_수있다() {
        // when
        PublicAccountDto account = accountService.findAccount_USER("test");

        // then
        assertEquals(account.getUsername(), "test");
        assertEquals(account.getEmail(), "test@email.com");
        assertEquals(account.getNickname(), "test-nick");
    }

    @Test
    void 없는_account_조회_404() {
        // given
        String username = "ghost";

        // when & then
        NotFoundException nfe = assertThrows(NotFoundException.class, () -> accountService.findMyAccount(username));
        assertEquals(nfe.getCode(), ErrorCode.NOT_FOUND_ACCOUNT.getCode());
    }

    @Test
    void account_수정할_수있다() {
        // given
        UpdateAccountRequest request = UpdateAccountRequest.builder()
                .username("test3")
                .password("pass3")
                .email("test3@email.com")
                .nickname("test3-nick")
                .build();

        // when
        Long id = accountService.updateAccount("test2", request);

        // then
        assertNotNull(id);
    }

    @Test
    void 없는_account_수정시_404() {
        // given
        String username = "ghost";

        // when & then
        assertThrows(NotFoundException.class, () -> accountService.updateAccount(username, null));
    }

    @Test
    @Order(Integer.MAX_VALUE - 1)
    void account_삭제할_수있다() {
        // when & then
        assertDoesNotThrow(() -> accountService.deleteAccount("test"));
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void account_삭제후_할일도_함께_삭제됐는지() {
        // given

        // when
        RetrieveTodoItemResponse result = todoService.findUserTodoList(PageRequest.of(0, 5), "test");

        // then
        assertEquals(result.getTodoList().size(), 0);
    }

    @Test
    void 없는_account_삭제_404() {
        // given
        String username = "ghost";

        // when & then
        NotFoundException nfe = assertThrows(NotFoundException.class, () -> accountService.deleteAccount(username));
        assertEquals(nfe.getCode(), ErrorCode.NOT_FOUND_ACCOUNT.getCode());
    }


}