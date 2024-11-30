package com.graphql.demo_graphql.model;

import lombok.Data;

@Data
public class AuthData {
    private String token;
    private String authorId;
}
