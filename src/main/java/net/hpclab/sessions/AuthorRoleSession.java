package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.AuthorRole;

@Stateless
public class AuthorRoleSession extends Generic<AuthorRole>{
    public AuthorRoleSession() {
        super(AuthorRole.class);
    }
}