package com.example.training.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("TrainingAdminRestControllerIntegrationTest.sql")
@Transactional
class TrainingAdminRestControllerIntegrationTestMockMvc {
    @Autowired
    MockMvc mockMvc;

    @Test
    void test_getTrainings() throws Exception {
        mockMvc.perform(
            get("/api/trainings")
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].title").value("ビジネスマナー研修"))
        .andExpect(jsonPath("$[1].title").value("Java実践"))
         ;
    }

    @Test
    void test_getTraining() throws Exception {
        mockMvc.perform(get("/api/trainings/{id}", "t01"))
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.id").value("t01"))
        	.andExpect(jsonPath("$.title").value("ビジネスマナー研修"))
            .andExpect(jsonPath("$.startDateTime").value("2021-08-01T09:30:00"))
            .andExpect(jsonPath("$.endDateTime").value("2021-08-03T17:00:00"))
            .andExpect(jsonPath("$.reserved").value("1"))
            .andExpect(jsonPath("$.capacity").value("10"));
        
    }
}