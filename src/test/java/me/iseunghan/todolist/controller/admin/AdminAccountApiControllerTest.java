package me.iseunghan.todolist.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminAccountApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void ADMIN은_모든_회원의_정보를_조회할_수있다_200() throws Exception {
        // when & then
        mockMvc.perform(get("/admin/accounts")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content.accountList[0].username").exists())
                .andExpect(jsonPath("content.accountList[0].email").exists())
                .andExpect(jsonPath("content.accountList[0].nickname").exists())
                .andExpect(jsonPath("content.accountList[0].role").exists())
                .andExpect(jsonPath("content.pageable").exists())
        ;
    }

    @Test
    @WithMockUser(roles = "USER")
    void USER는_모든_회원의_공개정보_외는_조회할_수없다_403() throws Exception {
        // when & then
        mockMvc.perform(get("/admin/accounts")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ADMIN은_하나의_회원을_상세조회_할수있다_200() throws Exception {
        // when & then
        mockMvc.perform(get("/admin/accounts/admin")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("content.username").exists())
                .andExpect(jsonPath("content.email").exists())
                .andExpect(jsonPath("content.nickname").exists())
                .andExpect(jsonPath("content.todoSize").exists())
                .andExpect(jsonPath("content.role").exists())
        ;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ADMIN은_하나의_회원을_상세조회_할수있다_404() throws Exception {
        // when & then
        mockMvc.perform(get("/admin/accounts/admin123")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("error").exists())
        ;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ADMIN은_하나의_회원을_삭제할_수있다() throws Exception {
        // when & then
        mockMvc.perform(delete("/admin/accounts/user"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}