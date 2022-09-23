package me.iseunghan.todolist.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.iseunghan.todolist.jwt.JwtTokenUtil;
import me.iseunghan.todolist.model.dto.CreateAccountRequest;
import me.iseunghan.todolist.model.dto.CreateAccountResponse;
import me.iseunghan.todolist.model.dto.UpdateAccountRequest;
import me.iseunghan.todolist.service.AccountService;
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
class UserAccountApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private List<CreateAccountResponse> testAccounts;

    @BeforeAll
    void setup() {
        // init
        testAccounts = new ArrayList<>();

        // set 3 Test User
        for (int i = 10; i < 13; i++) {
            CreateAccountRequest request = CreateAccountRequest.builder()
                    .username("test" + i)
                    .nickname("nick" + i)
                    .email("test" + i + "@email.com")
                    .password("test" + i)
                    .build();

            CreateAccountResponse response = accountService.addAccount(request);
            this.testAccounts.add(response);
        }
    }

    @Test
    void 새로운_회원을_추가할_수있다() throws Exception {
        // given
        CreateAccountRequest request = CreateAccountRequest.builder()
                .username("testUser100")
                .nickname("testNick100")
                .email("test100@email.com")
                .password("pass")
                .build();

        // when & then
        mockMvc.perform(post("/user/accounts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("content.username").value(request.getUsername()))
                .andExpect(jsonPath("content.email").value(request.getEmail()))
                .andExpect(jsonPath("content.nickname").value(request.getNickname()))
                ;
    }

    @Test
    @WithMockUser
    void USER는_모든_회원의_공개정보를_조회할_수있다() throws Exception {

        // when & then
        mockMvc.perform(get("/user/accounts")
                        .header("Authorization", "Bearer 12345"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("content.accountList[0].username").exists())
                .andExpect(jsonPath("content.pageable").exists())
        ;
    }

    @Test
    @WithMockUser
    void USER_자신의_정보를_조회할_수있다() throws Exception {
        // given
        CreateAccountResponse testAccount = this.testAccounts.get(0);   // "test10"
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn(testAccount.getUsername());

        // when & then
        mockMvc.perform(get("/user/accounts/{username}", testAccount.getUsername())
                        .header("Authorization", "Bearer 12345"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("content.id").exists())
                .andExpect(jsonPath("content.username").value(testAccount.getUsername()))
                .andExpect(jsonPath("content.nickname").value(testAccount.getNickname()))
                .andExpect(jsonPath("content.email").value(testAccount.getEmail()))
        ;
    }

    @Test
    @WithMockUser
    void USER_회원_정보를_수정할_수있다() throws Exception {
        // given
        CreateAccountResponse testAccount = this.testAccounts.get(1);
        UpdateAccountRequest editDto = UpdateAccountRequest.builder()
                .username("editName")
                .password("editPass")
                .email("edit@email.com")
                .nickname("editNick")
                .build();
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn(testAccount.getUsername());

        // when & then
        mockMvc.perform(patch("/user/accounts/{username}", testAccount.getUsername())
                        .header("Authorization", "Bearer 12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("content").exists())
        ;
    }

    @Test
    @WithMockUser
    void USER_자신의_계정을_삭제할_수있다() throws Exception {
        // given
        CreateAccountResponse testAccount = this.testAccounts.get(2);
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn(testAccount.getUsername());

        // when & then
        mockMvc.perform(delete("/user/accounts/{username}", testAccount.getUsername())
                        .header("Authorization", "Bearer 12345"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
        ;
    }

}