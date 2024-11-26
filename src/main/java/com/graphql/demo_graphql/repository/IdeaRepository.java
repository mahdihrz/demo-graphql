package com.graphql.demo_graphql.repository;

import com.graphql.demo_graphql.model.Idea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdeaRepository extends JpaRepository<Idea, Long> {
}
