package com.graphql.demo_graphql.model;

import com.graphql.demo_graphql.graphql.IdeaResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IdeaResolverTests {
    @Autowired
    private IdeaResolver ideaResolver;

    private Author authorForTests;

    @BeforeEach
    public void injectIdeas() {
        authorForTests = ideaResolver.createAuthor("firstAuthor", "mail-origin@mail.test");
        ideaResolver.createIdea("Test Idea", "Test Description", authorForTests.getId());
        ideaResolver.createIdea("Test Idea 2", "Test Description 2", authorForTests.getId());
    }

    @Test
    public void testCreateIdea() {
        Idea createdIdea = ideaResolver.createIdea("Test Idea", "Test Description", authorForTests.getId());
        assertThat(createdIdea.getTitle()).isEqualTo("Test Idea");
        assertThat(createdIdea.getDescription()).isEqualTo("Test Description");
        assertThat(createdIdea.getAuthor().getId()).isEqualTo(authorForTests.getId());
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

        Idea changedIdea = ideaResolver.updateIdea(1L, changedTitle, changedDescription, authorForTests.getId());
        assertThat(changedIdea).isNotNull();
        assertThat(changedIdea.getTitle()).isEqualTo(changedTitle);
        assertThat(changedIdea.getDescription()).isEqualTo(changedDescription);
        assertThat(changedIdea.getAuthor().getId()).isEqualTo(authorForTests.getId());

        Idea foundIdea = ideaResolver.getIdeaById(1L);
        assertThat(foundIdea).isNotNull();
        assertThat(foundIdea.getTitle()).isEqualTo(changedTitle);
        assertThat(foundIdea.getDescription()).isEqualTo(changedDescription);
        assertThat(foundIdea.getAuthor().getId()).isEqualTo(authorForTests.getId());
    }

    @Test
    public void testUpdateIdeaNotFound() {
        RuntimeException e = assertThrows(RuntimeException.class, () -> ideaResolver.updateIdea(3L, "test", "test", authorForTests.getId()));
        assertThat(e.getMessage()).isEqualTo("Idea with ID 3 not found");
    }

    @Test
    public void testDeleteIdea() {
        assertThat(ideaResolver.deleteIdeaById(1L)).isEqualTo(IdeaResolver.DELETED_SUCCESSFULLY);
        assertThat(ideaResolver.getIdeaById(1L)).isNull();
        assertThat(ideaResolver.getAllIdeas().size()).isEqualTo(1);
    }

    @Test
    public void testGetAuthors() {
        List<Author> authors = ideaResolver.getAuthors();
        assertThat(authors).isNotNull();
        assertThat(authors.size()).isEqualTo(1);
        assertThat(authors.getFirst().getId()).isEqualTo(authorForTests.getId());
    }

    @Test
    public void testGetAuthorById() {
        assertThat(ideaResolver.getAuthorById(1L).getName()).isEqualTo(authorForTests.getName());
        assertThat(ideaResolver.getAuthorById(1L).getEmail()).isEqualTo(authorForTests.getEmail());
    }

    @Test
    public void testcreateAuthor() {
        Author createdAuthor = ideaResolver.createAuthor("created_one", "test_create@mail.me");
        assertThat(createdAuthor).isNotNull();
        assertThat(ideaResolver.getAuthors().getLast().getId()).isEqualTo(createdAuthor.getId());
    }
}
