package com.registryserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@SpringBootTest(webEnvironment = DEFINED_PORT)
public class RegistryServerApplicationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    public void whenTheEndpointToGetTheListOfRegisteredApplicationsIsInvoked_thenOkHttpCodeIsReturned() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = testRestTemplate.getForEntity(
                "/eureka/apps",
                Map.class
        );
        assertThat(entity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }


    @Test
    public void whenHealthEndpointIsInvoked_thenOkHttpCodeIsReturned() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = testRestTemplate.getForEntity(
                "/actuator/health",
                Map.class
        );
        assertThat(entity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

}
