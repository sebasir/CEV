package net.hpclab.cev.services;

import java.io.Serializable;
import java.util.HashMap;
import net.hpclab.cev.entities.Users;

public class SessionService implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final HashMap<String, UserSession> ONLINE_USERS = new HashMap<>();

    public static void addUser(String sessionId, UserSession userSession) {
        ONLINE_USERS.put(sessionId, userSession);
    }

    public static void removeUser(String sessionId) {
        ONLINE_USERS.remove(sessionId);
    }

    public static boolean isUserOnline(Users users) {
        for (String sessionId : ONLINE_USERS.keySet()) {
            if (ONLINE_USERS.get(sessionId).getUser().equals(users)) {
                return true;
            }
        }
        return false;
    }
}
