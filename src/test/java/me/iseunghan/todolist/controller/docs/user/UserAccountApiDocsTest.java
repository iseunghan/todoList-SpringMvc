package me.iseunghan.todolist.controller.docs.user;

import me.iseunghan.todolist.config.SecurityConfig;
import me.iseunghan.todolist.controller.docs.RestDocumentSupport;
import me.iseunghan.todolist.controller.user.UserAccountApiController;
import me.iseunghan.todolist.jwt.JwtTokenUtil;
import me.iseunghan.todolist.model.dto.*;
import me.iseunghan.todolist.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserAccountApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )}
)
public class UserAccountApiDocsTest extends RestDocumentSupport {

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Test
    void addAccount() throws Exception {
        // given
        CreateAccountRequest request = CreateAccountRequest.builder()
                .username("testUser100")
                .nickname("testNick100")
                .email("test100@email.com")
                .password("pass")
                .build();
        CreateAccountResponse response = CreateAccountResponse.builder()
                .username("testUser100")
                .nickname("testNick100")
                .email("test100@email.com")
                .build();
        given(accountService.addAccount(any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/user/accounts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestFields(
                                        fieldWithPath("username").description("아이디"),
                                        fieldWithPath("password").description("패스워드"),
                                        fieldWithPath("email").description("이메일"),
                                        fieldWithPath("nickname").description("닉네임")
                                ),
                                responseFields(
                                        fieldWithPath("username").description("아이디"),
                                        fieldWithPath("email").description("이메일"),
                                        fieldWithPath("nickname").description("닉네임")
                                )
                        )
                )
                .andExpect(status().isCreated())
        ;
    }

    @Test
    @WithMockUser
    void retrieve_Any_PublicAccount() throws Exception {
        // given
        PublicAccountDto dto1 = PublicAccountDto.builder()
                .username("john1234")
                .email("john@email.com")
                .nickname("john")
                .todoSize(5)
                .build();
        PublicAccountDto dto2 = PublicAccountDto.builder()
                .username("james1234")
                .email("james@email.com")
                .nickname("james")
                .todoSize(12)
                .build();
        RetrieveAccountResponse<PublicAccountDto> response = RetrieveAccountResponse.<PublicAccountDto>builder()
                .accountList(List.of(dto1, dto2))
                .pageable(PageDto.builder()
                        .number(0)
                        .totalPages(1)
                        .totalElements(2)
                        .first(true)
                        .last(true)
                        .build())
                .build();
        given(jwtTokenUtil.isCorrectUsername(anyString(), anyString())).willReturn(true);
        given(accountService.findAll_USER(any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/user/accounts")
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰 값")
                                ),
                                responseFields(
                                        fieldWithPath("accountList[].username").description("아이디"),
                                        fieldWithPath("accountList[].email").description("이메일"),
                                        fieldWithPath("accountList[].nickname").description("닉네임"),
                                        fieldWithPath("accountList[].todoSize").description("할일 개수"),
                                        fieldWithPath("pageable.number").description("현재 페이지 번호"),
                                        fieldWithPath("pageable.totalElements").description("전체 사용자 수"),
                                        fieldWithPath("pageable.totalPages").description("전체 페이지 수"),
                                        fieldWithPath("pageable.first").description("첫번째 페이지 여부"),
                                        fieldWithPath("pageable.last").description("마지막 페이지 여부")
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    @WithMockUser
    void retrieve_My_Account() throws Exception {
        // given
        AccountDto accountDto = AccountDto.builder()
                .id(5L)
                .username("john1234")
                .password("pass")
                .email("test@email.com")
                .nickname("nickname")
                .roles("USER")
                .todoList(null)
                .build();
        given(jwtTokenUtil.isCorrectUsername(anyString(), anyString())).willReturn(true);
        given(accountService.findMyAccount(anyString())).willReturn(accountDto);

        // when & then
        mockMvc.perform(get("/user/accounts/{username}", "john1234")
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
                                        fieldWithPath("id").description("idx"),
                                        fieldWithPath("username").description("아이디"),
                                        fieldWithPath("password").description("패스워드"),
                                        fieldWithPath("nickname").description("닉네임"),
                                        fieldWithPath("email").description("이메일"),
                                        fieldWithPath("roles").description("권한"),
                                        fieldWithPath("todoList").ignored()
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    @WithMockUser
    void update_Account() throws Exception {
        // given
        UpdateAccountRequest editDto = UpdateAccountRequest.builder()
                .username("editName")
                .password("editPass")
                .email("edit@email.com")
                .nickname("editNick")
                .build();
        given(jwtTokenUtil.isCorrectUsername(anyString(), anyString())).willReturn(true);
        given(accountService.updateAccount(anyString(), any())).willReturn(5L);

        // when & then
        mockMvc.perform(patch("/user/accounts/{username}", "username1234")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editDto)))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰 값")
                                ),
                                pathParameters(
                                        parameterWithName("username").description("사용자 아이디")
                                ),
                                requestFields(
                                        fieldWithPath("username").description("변경할 아이디").optional(),
                                        fieldWithPath("password").description("변경할 패스워드").optional(),
                                        fieldWithPath("email").description("변경할 이메일").optional(),
                                        fieldWithPath("nickname").description("변경할 넥네임").optional()
                                ),
                                responseFields(
                                        fieldWithPath("id").description("사용자 idx")
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    @WithMockUser
    void delete_My_Account() throws Exception {
        // given
        given(jwtTokenUtil.isCorrectUsername(anyString(), anyString())).willReturn(true);
        given(accountService.deleteAccount(anyString())).willReturn(6L);

        // when & then
        mockMvc.perform(delete("/user/accounts/{username}", "username1234")
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
                                        fieldWithPath("id").description("삭제된 사용자 idx")
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }
}
