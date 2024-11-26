package com.graphql.demo_graphql.graphql;

import com.graphql.demo_graphql.model.Author;
import com.graphql.demo_graphql.model.Idea;
import com.graphql.demo_graphql.repository.AuthorRepository;
import com.graphql.demo_graphql.repository.IdeaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class IdeaResolver {

    public static final String DELETED_SUCCESSFULLY = "Deleted Successfully";

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @QueryMapping
    public List<Idea> getAllIdeas() {
        return ideaRepository.findAll();
    }

    @QueryMapping
    public Idea getIdeaById(@Argument final Long id) {
        return ideaRepository.findById(id).orElse(null);
    }

    @MutationMapping
    public Idea createIdea(@Argument final String title, @Argument final String description, @Argument final Long authorId) {
        Optional<Author> author = authorRepository.findById(authorId);
        if (author.isPresent()){
            Idea idea = new Idea();
            idea.setTitle(title);
            idea.setDescription(description);
            idea.setAuthor(author.get());
            return ideaRepository.save(idea);
        }
        throw new RuntimeException(String.format("Author ID : %d not found", authorId));
    }

    @MutationMapping
    public Idea updateIdea(@Argument final Long id, @Argument final String title, @Argument final String description, @Argument final Long authorId) {
        Optional<Idea> optionalIdea = ideaRepository.findById(id);
        if (optionalIdea.isPresent()) {
            Idea idea = optionalIdea.get();
            if (title != null) idea.setTitle(title);
            if (description != null) idea.setDescription(description);
            if (authorId != null) {
                Optional<Author> author = authorRepository.findById(authorId);
                author.ifPresent(idea::setAuthor);
            }
            return ideaRepository.save(idea);
        }
        throw new RuntimeException("Idea with ID " + id + " not found");
    }

    @MutationMapping
    public String deleteIdeaById(final Long id) {
        ideaRepository.deleteById(id);
        return DELETED_SUCCESSFULLY;
    }

    @QueryMapping
    public List<Author> getAuthors() {
        return authorRepository.findAll();
    }

    @QueryMapping
    public Author getAuthorById(@Argument Long id) {
        Optional<Author> author = authorRepository.findById(id);
        return author.orElse(null);
    }

    @MutationMapping
    public Author createAuthor(@Argument String name, @Argument String email) {
        Author author = new Author();
        author.setName(name);
        author.setEmail(email);
        return authorRepository.save(author);
    }
}
