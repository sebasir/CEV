package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.LocationLevel;

@Stateless
public class LocationLevelSession extends Generic<LocationLevel>{
    public LocationLevelSession() {
        super(LocationLevel.class);
    }
}