package com.security.custom.repository;

import com.security.custom.configuration.persistence.PersistenceConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(PersistenceConfiguration.class)
public class ApplicationClientDetailsRepositoryTest {

    @Autowired
    private ApplicationClientDetailsRepository repository;


    @Test
    @DisplayName("test")
    public void test() {
        int a = 1;
    }

}
