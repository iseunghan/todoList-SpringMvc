package me.iseunghan.todolist.controller.docs.user;

import me.iseunghan.todolist.common.LoginUserArgumentResolver;
import me.iseunghan.todolist.config.SecurityConfig;
import me.iseunghan.todolist.controller.admin.AdminTodoListApiController;
import me.iseunghan.todolist.controller.docs.RestDocumentSupport;
import me.iseunghan.todolist.controller.user.UserAccountApiController;
import me.iseunghan.todolist.exception.NotFoundException;
import me.iseunghan.todolist.exception.model.ErrorCode;
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
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserAccountApiController.class)
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
                                        fieldWithPath("username").description("아이디").attributes(key("required").value(true)),
                                        fieldWithPath("password").description("패스워드").attributes(key("required").value(true)),
                                        fieldWithPath("email").description("이메일").attributes(key("required").value(true)),
                                        fieldWithPath("nickname").description("닉네임").attributes(key("required").value(true))
                                ),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부 (true/false)"),
                                        fieldWithPath("content.username").description("아이디"),
                                        fieldWithPath("content.email").description("이메일"),
                                        fieldWithPath("content.nickname").description("닉네임")
                                )
                        )
                )
                .andExpect(status().isCreated())
        ;
    }

    @Test
    void addAccount_400() throws Exception {
        // given
        CreateAccountRequest request = CreateAccountRequest.builder()
                .username("")
                .nickname("")
                .email("")
                .password("")
                .build();

        // when & then
        mockMvc.perform(post("/user/accounts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestFields(
                                        fieldWithPath("username").description("아이디").attributes(key("required").value(true)),
                                        fieldWithPath("password").description("패스워드").attributes(key("required").value(true)),
                                        fieldWithPath("email").description("이메일").attributes(key("required").value(true)),
                                        fieldWithPath("nickname").description("닉네임").attributes(key("required").value(true))
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
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
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
                                        fieldWithPath("success").description("성공 여부 (true/false)"),
                                        fieldWithPath("content.accountList[].username").description("아이디"),
                                        fieldWithPath("content.accountList[].email").description("이메일"),
                                        fieldWithPath("content.accountList[].nickname").description("닉네임"),
                                        fieldWithPath("content.accountList[].todoSize").description("할일 개수"),
                                        fieldWithPath("content.pageable.number").description("현재 페이지 번호"),
                                        fieldWithPath("content.pageable.totalElements").description("전체 사용자 수"),
                                        fieldWithPath("content.pageable.totalPages").description("전체 페이지 수"),
                                        fieldWithPath("content.pageable.first").description("첫번째 페이지 여부"),
                                        fieldWithPath("content.pageable.last").description("마지막 페이지 여부")
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
        RetrieveMyAccountResponse accountDto = RetrieveMyAccountResponse.builder()
                .id(5L)
                .username("john1234")
                .email("test@email.com")
                .nickname("nickname")
                .roles("USER")
                .build();
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn(accountDto.getUsername());
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
                                        fieldWithPath("success").description("성공 여부 (true/false)"),
                                        fieldWithPath("content.id").description("idx"),
                                        fieldWithPath("content.username").description("아이디"),
                                        fieldWithPath("content.nickname").description("닉네임"),
                                        fieldWithPath("content.email").description("이메일"),
                                        fieldWithPath("content.roles").description("권한")
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    @WithMockUser
    void retrieve_My_Account_403() throws Exception {
        // given

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
    @WithMockUser
    void update_Account() throws Exception {
        // given
        UpdateAccountRequest editDto = UpdateAccountRequest.builder()
                .username("editName")
                .password("editPass")
                .email("edit@email.com")
                .nickname("editNick")
                .build();
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn("username1234");
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
                                        fieldWithPath("username").description("변경할 아이디").attributes(key("required").value(false)),
                                        fieldWithPath("password").description("변경할 패스워드").attributes(key("required").value(false)),
                                        fieldWithPath("email").description("변경할 이메일").attributes(key("required").value(false)),
                                        fieldWithPath("nickname").description("변경할 넥네임").attributes(key("required").value(false))
                                ),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부 (true/false)"),
                                        fieldWithPath("content").description("사용자 idx")
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    @WithMockUser
    void update_Account_403() throws Exception {
        // given
        UpdateAccountRequest editDto = UpdateAccountRequest.builder()
                .username("editUser")
                .password("editPass")
                .email("edit@email.com")
                .nickname("editNick")
                .build();

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
                                        fieldWithPath("username").description("변경할 아이디").attributes(key("required").value(false)),
                                        fieldWithPath("password").description("변경할 패스워드").attributes(key("required").value(false)),
                                        fieldWithPath("email").description("변경할 이메일").attributes(key("required").value(false)),
                                        fieldWithPath("nickname").description("변경할 넥네임").attributes(key("required").value(false))
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
    @WithMockUser
    void delete_My_Account() throws Exception {
        // given
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn("username1234");
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
                                        fieldWithPath("success").description("성공 여부 (true/false)")
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    @WithMockUser
    void delete_My_Account_404() throws Exception {
        // given
        given(jwtTokenUtil.extractToken(any())).willReturn("token");
        given(jwtTokenUtil.getUsernameFromToken(anyString())).willReturn("username1234");
        given(accountService.deleteAccount(anyString())).willThrow(new NotFoundException(ErrorCode.NOT_FOUND_ACCOUNT));

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
                                        fieldWithPath("success").description("성공 여부 (true/false)"),
                                        fieldWithPath("error.code").description("에러 코드"),
                                        fieldWithPath("error.message").description("에러 메세지")
                                )
                        )
                )
                .andExpect(status().isNotFound())
        ;
    }
}
