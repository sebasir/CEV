package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.Location;

@Stateless
public class LocationSession extends Generic<Location>{
    public LocationSession() {
        super(Location.class);
    }
}