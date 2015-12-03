package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.SpecimenContent;

@Stateless
public class SpecimenContentSession extends Generic<SpecimenContent>{
    public SpecimenContentSession() {
        super(SpecimenContent.class);
    }
}