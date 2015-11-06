package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.Taxonomy;

@Stateless
public class TaxonomySession extends Generic<Taxonomy>{
    public TaxonomySession() {
        super(Taxonomy.class);
    }
}