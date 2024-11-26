package com.graphql.demo_graphql.repository;

import com.graphql.demo_graphql.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
