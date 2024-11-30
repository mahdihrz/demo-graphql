package com.graphql.demo_graphql.client;

import com.graphql.demo_graphql.model.Idea;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/data-for-client.sql")
public class GraphQLClientTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    private GraphQLClient graphQLClient;

    @PostConstruct
    public void configureGraphQLClient() {
        graphQLClient.setPort(port);
    }

    @Test
    public void testGetIdeas() {

        graphQLClient.createUser("userName", "mail@mail.com", "password");

        String token = graphQLClient.login("mail@mail.com", "password");

        List<Idea> ideas = graphQLClient.getIdeas(token);
        assertThat(ideas).isNotNull();
        assertThat(ideas.size()).isEqualTo(1);
        assertThat(ideas.getFirst().getTitle()).isEqualTo("title data");
        assertThat(ideas.getFirst().getAuthor().getId()).isEqualTo(2);
        assertThat(ideas.getFirst().getAuthor().getName()).isEqualTo("test_injected");
        assertThat(ideas.getFirst().getAuthor().getEmail()).isEqualTo("test_injected@mail.me");
    }
}