package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.AuthorType;

@Stateless
public class AuthorTypeSession extends Generic<AuthorType>{
    public AuthorTypeSession() {
        super(AuthorType.class);
    }
}