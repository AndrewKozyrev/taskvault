package org.landsreyk.taskvault.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.landsreyk.taskvault.auth.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BearerAuthIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private String loginAndGetToken(String username, String password) throws Exception {
        var body = """
                {
                   "username": "%s",
                   "password": "%s"
                }
                """.formatted(username, password);
        var response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().getResponse().getContentAsString();
        var authResponse = objectMapper.readValue(response, AuthResponse.class);
        return authResponse.getAccessToken();
    }

    @Test
    void protectedEndpoint_withValidBearerToken_returns200() throws Exception {
        var token = loginAndGetToken("user", "password");
        mockMvc.perform(get("/api/secure/ping")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    @Test
    void protectedEndpoint_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/secure/ping"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("error").value("unauthorized"));
    }

    @Test
    void protectedEndpoint_withMalformedHeader_returns401() throws Exception {
        mockMvc.perform(get("/api/secure/ping")
                        .header("Authorization", "BearerX something"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("error").value("unauthorized"));
    }

    @Test
    void protectedEndpoint_withTamperedToken_returns401() throws Exception {
        var token = loginAndGetToken("user", "password");
        token = token + "1";
        mockMvc.perform(get("/api/secure/ping")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }
}
