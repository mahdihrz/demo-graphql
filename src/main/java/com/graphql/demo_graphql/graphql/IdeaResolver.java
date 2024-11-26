package com.graphql.demo_graphql.graphql;

import com.graphql.demo_graphql.model.Idea;
import com.graphql.demo_graphql.repository.IdeaRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @QueryMapping
    public List<Idea> getAllIdeas() {
        return ideaRepository.findAll();
    }

    @QueryMapping
    public Idea getIdeaById(final Long id) {
        return ideaRepository.findById(id).orElse(null);
    }

    @MutationMapping
    public Idea createIdea(final String title, final String description) {
        Idea idea = new Idea();
        idea.setTitle(title);
        idea.setDescription(description);
        return ideaRepository.save(idea);
    }

    @MutationMapping
    public Idea updateIdea(final Long id, final String title, final String description) {
        Optional<Idea> optionalIdea = ideaRepository.findById(id);
        if (optionalIdea.isPresent()) {
            Idea idea = optionalIdea.get();
            if (title != null) idea.setTitle(title);
            if (description != null) idea.setDescription(description);
            return ideaRepository.save(idea);
        }
        throw new RuntimeException("Idea with ID " + id + " not found");
    }

    @MutationMapping
    public String deleteIdeaById(final Long id) {
        ideaRepository.deleteById(id);
        return DELETED_SUCCESSFULLY;
    }
}
