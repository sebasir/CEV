package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.EnvConf;

@Stateless
public class EnvConfSession extends Generic<EnvConf>{
    public EnvConfSession() {
        super(EnvConf.class);
    }
}