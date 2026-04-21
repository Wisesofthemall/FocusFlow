package com.blackcs.propath.web;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private String jwt;

  @BeforeEach
  void registerUserAndGetToken() throws Exception {
    String unique = "app-test-" + System.nanoTime() + "@propath.local";
    String registerBody =
        """
        {"name":"App Tester","email":"%s","password":"Passw0rd!"}
        """.formatted(unique);

    MvcResult res =
        mockMvc
            .perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registerBody))
            .andExpect(status().isCreated())
            .andReturn();

    JsonNode body = objectMapper.readTree(res.getResponse().getContentAsString());
    jwt = body.get("token").asText();
  }

  @Test
  void createThenGetById_returnsTheSameApplication() throws Exception {
    String createBody =
        """
        {
          "company": "Acme Corp",
          "roleTitle": "Software Engineer",
          "status": "APPLIED",
          "nextActionDate": "2026-05-01T09:00:00",
          "priority": "HIGH"
        }
        """;

    MvcResult created =
        mockMvc
            .perform(
                post("/api/applications")
                    .header("Authorization", "Bearer " + jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.company").value("Acme Corp"))
            .andExpect(jsonPath("$.status").value("APPLIED"))
            .andReturn();

    Long id =
        objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

    mockMvc
        .perform(get("/api/applications/" + id).header("Authorization", "Bearer " + jwt))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.roleTitle").value("Software Engineer"));
  }

  @Test
  void updateThenDelete_changesStatusThenRemovesIt() throws Exception {
    String createBody =
        """
        {
          "company": "Globex",
          "roleTitle": "Backend Engineer",
          "status": "APPLIED",
          "nextActionDate": "2026-05-10T10:00:00",
          "priority": "MEDIUM"
        }
        """;
    MvcResult created =
        mockMvc
            .perform(
                post("/api/applications")
                    .header("Authorization", "Bearer " + jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createBody))
            .andExpect(status().isCreated())
            .andReturn();
    Long id =
        objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

    String updateBody =
        """
        {
          "company": "Globex",
          "roleTitle": "Backend Engineer",
          "status": "INTERVIEWING",
          "nextActionDate": "2026-05-15T13:00:00",
          "priority": "HIGH"
        }
        """;
    mockMvc
        .perform(
            put("/api/applications/" + id)
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("INTERVIEWING"))
        .andExpect(jsonPath("$.priority").value("HIGH"));

    mockMvc
        .perform(delete("/api/applications/" + id).header("Authorization", "Bearer " + jwt))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get("/api/applications/" + id).header("Authorization", "Bearer " + jwt))
        .andExpect(status().is4xxClientError());
  }
}
