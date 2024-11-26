package com.graphql.demo_graphql.model;

import com.graphql.demo_graphql.graphql.IdeaResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IdeaResolverTests {
    @Autowired
    private IdeaResolver ideaResolver;

    @BeforeEach
    public void injectIdeas() {
        ideaResolver.createIdea("Test Idea", "Test Description");
        ideaResolver.createIdea("Test Idea 2", "Test Description 2");
    }

    @Test
    public void testCreateIdea() {
        Idea createdIdea = ideaResolver.createIdea("Test Idea", "Test Description");
        assertThat(createdIdea.getTitle()).isEqualTo("Test Idea");
        assertThat(createdIdea.getDescription()).isEqualTo("Test Description");
    }

    @Test
    public void testGetIdeaById() {

        Idea foundIdea = ideaResolver.getIdeaById(1L);
        assertThat(foundIdea).isNotNull();
        assertThat(foundIdea.getTitle()).isEqualTo("Test Idea");
    }

    @Test
    public void testUpdateIdea() {

        final String changedTitle = "Changed title";
        final String changedDescription = "Changed description";

        Idea changedIdea = ideaResolver.updateIdea(1L, changedTitle, changedDescription);
        assertThat(changedIdea).isNotNull();
        assertThat(changedIdea.getTitle()).isEqualTo(changedTitle);
        assertThat(changedIdea.getDescription()).isEqualTo(changedDescription);

        Idea foundIdea = ideaResolver.getIdeaById(1L);
        assertThat(foundIdea).isNotNull();
        assertThat(foundIdea.getTitle()).isEqualTo(changedTitle);
        assertThat(foundIdea.getDescription()).isEqualTo(changedDescription);
    }

    @Test
    public void testUpdateIdeaNotFound() {
        RuntimeException e = assertThrows(RuntimeException.class, () -> ideaResolver.updateIdea(3L, "test", "test"));
        assertThat(e.getMessage()).isEqualTo("Idea with ID 3 not found");
    }

    @Test
    public void testDeleteIdea() {

        assertThat(ideaResolver.deleteIdeaById(1L)).isEqualTo(IdeaResolver.DELETED_SUCCESSFULLY);
        assertThat(ideaResolver.getIdeaById(1L)).isNull();
        assertThat(ideaResolver.getAllIdeas().size()).isEqualTo(1);
    }
}
