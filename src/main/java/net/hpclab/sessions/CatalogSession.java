package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.Catalog;

@Stateless
public class CatalogSession extends Generic<Catalog>{
    public CatalogSession() {
        super(Catalog.class);
    }
}