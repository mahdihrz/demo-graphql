package com.graphql.demo_graphql.graphql;

import com.graphql.demo_graphql.model.AuthData;
import com.graphql.demo_graphql.model.Author;
import com.graphql.demo_graphql.model.Idea;
import com.graphql.demo_graphql.repository.AuthorRepository;
import com.graphql.demo_graphql.repository.IdeaRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class IdeaResolver {

    public static final String DELETED_SUCCESSFULLY = "Deleted Successfully";

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<Idea> getAllIdeas() {
        return ideaRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @QueryMapping
    public Idea getIdeaById(@Argument final Long id) {
        return ideaRepository.findById(id).orElse(null);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
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

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
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

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @MutationMapping
    public String deleteIdeaById(final Long id) {
        ideaRepository.deleteById(id);
        return DELETED_SUCCESSFULLY;
    }

    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<Author> getAuthors() {
        return authorRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @QueryMapping
    public Author getAuthorById(@Argument Long id) {
        Optional<Author> author = authorRepository.findById(id);
        return author.orElse(null);
    }

    @PreAuthorize("permitAll()")
    @MutationMapping
    public Author createAuthor(@Argument String name, @Argument String email, @Argument String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Author author = new Author();
        author.setName(name);
        author.setEmail(email);
        author.setPassword(passwordEncoder.encode(password));
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            author.setRoles(List.of("ADMIN","USER"));
        } else {
            author.setRoles(List.of("USER"));
        }
        return authorRepository.save(author);
    }

    @PreAuthorize("permitAll()")
    @MutationMapping
    public AuthData login(@Argument String email, @Argument String password) {
        Optional<Author> author = authorRepository.findByEmail(email);
        if (author.isPresent() && passwordEncoder.matches(password, author.get().getPassword())) {
            String roles = author.get().getRoles().stream()
                    .map(role -> "ROLE_" + role)
                    .collect(Collectors.joining(","));

            AuthData authData = new AuthData();
            authData.setToken(Jwts.builder()
                    .subject(email)
                    .claim("roles", roles)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                    .signWith(SignatureAlgorithm.HS256, "secretsecretsecretsecretsecretsecretsecretsecret")
                    .compact());
            authData.setAuthorId(author.get().getId().toString());
            return authData;
        }
        throw new RuntimeException("Invalid credentials");
    }
}
