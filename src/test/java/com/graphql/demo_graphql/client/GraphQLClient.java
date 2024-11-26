package com.graphql.demo_graphql.client;

import com.graphql.demo_graphql.model.Idea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GraphQLClient {

    private GraphQlClient graphQlClient;

    @Autowired
    public GraphQLClient() {
        this.graphQlClient = buildWebClient(80);
    }

    public void setPort(int port){
        this.graphQlClient = buildWebClient(port);
    }

    private HttpGraphQlClient buildWebClient(int port){
        return HttpGraphQlClient.builder()
                .url("http://localhost:" + port + "/graphql")
                .build();
    }

    public List<Idea> getIdeas() {

        String query = """
            query {
                getAllIdeas {
                    title
                    description
                    author {
                        id
                        name
                        email
                    }
                }
            }
        """;

        ParameterizedTypeReference<List<Idea>> typeListIdeaRef = new ParameterizedTypeReference<>() {};

        return graphQlClient.document(query)
                .retrieve("getAllIdeas")
                .toEntity(typeListIdeaRef)
                .block();

    }
}
