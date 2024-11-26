package com.graphql.demo_graphql.client;

import com.graphql.demo_graphql.model.Idea;
import jakarta.annotation.PostConstruct;
import org.assertj.core.api.Assert;
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
        List<Idea> ideas = graphQLClient.getIdeas();
        assertThat(ideas).isNotNull();
        assertThat(ideas.size()).isEqualTo(1);
        assertThat(ideas.getFirst().getTitle()).isEqualTo("title data");
    }
}