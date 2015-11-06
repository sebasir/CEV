package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.RegType;

@Stateless
public class RegTypeSession extends Generic<RegType>{
    public RegTypeSession() {
        super(RegType.class);
    }
}