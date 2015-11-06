package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.TaxonomyLevel;

@Stateless
public class TaxonomyLevelSession extends Generic<TaxonomyLevel>{
    public TaxonomyLevelSession() {
        super(TaxonomyLevel.class);
    }
}