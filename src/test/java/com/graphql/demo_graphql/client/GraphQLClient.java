package com.graphql.demo_graphql.client;

import com.graphql.demo_graphql.model.AuthData;
import com.graphql.demo_graphql.model.Author;
import com.graphql.demo_graphql.model.Idea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class GraphQLClient {

    private GraphQlClient graphQlClient;

    @Autowired
    public GraphQLClient() {
        this.graphQlClient = buildWebClient(80);
    }

    public void setPort(int port) {
        this.graphQlClient = buildWebClient(port);
    }

    private HttpGraphQlClient buildWebClient(int port) {
        return HttpGraphQlClient.builder()
                .url("http://localhost:" + port + "/graphql")
                .header("Content-Type", "application/json")
                .build();
    }

    public void createUser(String userName, String email, String password) {

        String createAuthorMutation = String.format("""
                    mutation {
                        createAuthor(name: "%s", email: "%s", password: "%s") {
                            id
                            name
                            email
                        }
                    }
                """, userName, email, password);
        graphQlClient.document(createAuthorMutation)
                .retrieve("createAuthor")
                .toEntity(Author.class)
                .doOnNext(System.out::println)
                .block();
    }

    public String login(String email, String password) {
        String loginMutation = String.format("""
                    mutation {
                        login(email: "%s", password: "%s"){
                            token
                        }
                    }
                """, email, password);

        return Objects.requireNonNull(graphQlClient.document(loginMutation)
                        .retrieve("login")
                        .toEntity(AuthData.class)
                        .block())
                .getToken();
    }

    public List<Idea> getIdeas(String token) {

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

        ParameterizedTypeReference<List<Idea>> typeListIdeaRef = new ParameterizedTypeReference<>() {
        };

        return ((HttpGraphQlClient.Builder<?>) graphQlClient.mutate())
                .header("Authorization", "Bearer " + token)
                .build()
                .document(query)
                .retrieve("getAllIdeas")
                .toEntity(typeListIdeaRef)
                .block();
    }
}
