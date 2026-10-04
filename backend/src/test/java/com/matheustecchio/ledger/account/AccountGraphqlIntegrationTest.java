package com.matheustecchio.ledger.account;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Tag("integration")
@Testcontainers
@AutoConfigureHttpGraphQlTester
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountGraphqlIntegrationTest {

    @Container
    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer(DockerImageName.parse("postgres:17.6-alpine"));

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @Test
    void createsPersistsAndRetrievesAnAccountThroughGraphql() {
        graphQlTester.document("""
                        mutation {
                          createAccount(input: {
                            name: "Everyday account"
                            type: CURRENT
                            currency: "EUR"
                            initialBalance: "425.50"
                          }) {
                            id name type currency initialBalance archived
                          }
                        }
                        """)
                .execute()
                .path("createAccount.name").entity(String.class).isEqualTo("Everyday account")
                .path("createAccount.initialBalance").entity(String.class).isEqualTo("425.5000");

        graphQlTester.document("""
                        query {
                          accounts { id name type currency initialBalance archived }
                        }
                        """)
                .execute()
                .path("accounts[0].name").entity(String.class).isEqualTo("Everyday account")
                .path("accounts[0].currency").entity(String.class).isEqualTo("EUR");
    }
}
