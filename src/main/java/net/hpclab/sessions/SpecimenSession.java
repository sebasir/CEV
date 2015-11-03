package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.Specimen;

@Stateless
public class SpecimenSession extends Generic<Specimen>{
    public SpecimenSession() {
        super(Specimen.class);
    }
}