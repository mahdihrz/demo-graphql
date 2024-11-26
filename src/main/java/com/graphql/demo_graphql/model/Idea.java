package com.graphql.demo_graphql.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@ToString(exclude = "author")
@NoArgsConstructor
@Entity
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

}

