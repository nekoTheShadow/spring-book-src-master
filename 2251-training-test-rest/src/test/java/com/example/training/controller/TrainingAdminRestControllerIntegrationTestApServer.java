package com.example.training.controller;


import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import com.example.training.entity.Training;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("TrainingAdminRestControllerIntegrationTest.sql")
@Sql(value = "clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TrainingAdminRestControllerIntegrationTestApServer {
    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void test_getTrainings() {
        ResponseEntity<Training[]> responseEntity = testRestTemplate.getForEntity("/api/trainings", Training[].class);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Training[] trainings = responseEntity.getBody();
        Assertions.assertThat(trainings.length).isEqualTo(3);
        Assertions.assertThat(trainings[0].getTitle()).isEqualTo("ビジネスマナー研修");
        Assertions.assertThat(trainings[1].getTitle()).isEqualTo("Java実践");
    }

    @Test
    void test_getTraining() throws Exception {
    	ResponseEntity<Training> responseEntity = testRestTemplate.getForEntity("/api/trainings/t01", Training.class);
    	assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    	
    	Training training = responseEntity.getBody();
		assertThat(training.getId()).isEqualTo("t01");
		assertThat(training.getTitle()).isEqualTo("ビジネスマナー研修");
		assertThat(training.getStartDateTime()).isEqualTo(LocalDateTime.of(2021, 8, 1, 9, 30));
		assertThat(training.getEndDateTime()).isEqualTo(LocalDateTime.of(2021, 8, 3, 17, 0));
		assertThat(training.getReserved()).isEqualTo(1);
		assertThat(training.getCapacity()).isEqualTo(10);
        
    }
}