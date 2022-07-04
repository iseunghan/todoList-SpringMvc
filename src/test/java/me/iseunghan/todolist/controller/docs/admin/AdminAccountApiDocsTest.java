package me.iseunghan.todolist.controller.docs.admin;


import me.iseunghan.todolist.config.SecurityConfig;
import me.iseunghan.todolist.controller.admin.AdminAccountApiController;
import me.iseunghan.todolist.controller.docs.RestDocumentSupport;
import me.iseunghan.todolist.jwt.JwtTokenUtil;
import me.iseunghan.todolist.model.dto.AdminAccountDto;
import me.iseunghan.todolist.model.dto.PageDto;
import me.iseunghan.todolist.model.dto.RetrieveAccountResponse;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminAccountApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )}
)
public class AdminAccountApiDocsTest extends RestDocumentSupport {

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Test
    @WithMockUser(roles = "ADMIN")
    void Retrieve_AccountList() throws Exception {
        // given
        AdminAccountDto dto1 = AdminAccountDto.builder()
                .username("john1234")
                .email("john@email.com")
                .nickname("john")
                .todoSize(5)
                .role("USER")
                .build();
        AdminAccountDto dto2 = AdminAccountDto.builder()
                .username("james1234")
                .email("james@email.com")
                .nickname("james")
                .todoSize(12)
                .role("USER")
                .build();
        RetrieveAccountResponse<AdminAccountDto> response = RetrieveAccountResponse.<AdminAccountDto>builder()
                .accountList(List.of(dto1, dto2))
                .pageable(PageDto.builder()
                        .number(0)
                        .totalPages(1)
                        .totalElements(2)
                        .first(true)
                        .last(true)
                        .build())
                .build();

        given(accountService.findAll_ADMIN(any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/accounts")
                        .header("Authorization", TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰 값")
                                ),
                                responseFields(
                                        fieldWithPath("accountList[].username").description("아이디"),
                                        fieldWithPath("accountList[].email").description("이메일"),
                                        fieldWithPath("accountList[].nickname").description("닉네임"),
                                        fieldWithPath("accountList[].role").description("권한"),
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
    @WithMockUser(roles = "ADMIN")
    void Retrieve_Account() throws Exception {
        // given
        AdminAccountDto dto =  AdminAccountDto.builder()
                .username("james1234")
                .email("james@email.com")
                .nickname("james")
                .todoSize(12)
                .role("USER")
                .build();

        given(accountService.findAccount_ADMIN(anyString())).willReturn(dto);

        // when & then
        mockMvc.perform(get("/admin/accounts/{username}", "james1234")
                        .header("Authorization", TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰 값")
                                ),
                                responseFields(
                                        fieldWithPath("username").description("아이디"),
                                        fieldWithPath("email").description("이메일"),
                                        fieldWithPath("nickname").description("닉네임"),
                                        fieldWithPath("role").description("권한"),
                                        fieldWithPath("todoSize").description("할일 개수")
                                )
                        )
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void Delete_Account() throws Exception {
        // given
        given(accountService.deleteAccount(anyString())).willReturn(6L);

        // when & then
        mockMvc.perform(delete("/admin/accounts/{username}", "james1234")
                        .header("Authorization", TOKEN))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT 토큰 값")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("삭제된 사용자 idx")
                                )
                        )
                )
                .andExpect(status().isOk());
    }
}
