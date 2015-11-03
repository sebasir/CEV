package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.Collection;

@Stateless
public class CollectionSession extends Generic<Collection>{
    public CollectionSession() {
        super(Collection.class);
    }
}