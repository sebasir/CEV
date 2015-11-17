package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.Author;

@Stateless
public class AuthorSession extends Generic<Author>{
    public AuthorSession() {
        super(Author.class);
    }
}