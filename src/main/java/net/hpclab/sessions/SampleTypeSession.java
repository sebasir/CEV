package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.SampleType;

@Stateless
public class SampleTypeSession extends Generic<SampleType>{
    public SampleTypeSession() {
        super(SampleType.class);
    }
}