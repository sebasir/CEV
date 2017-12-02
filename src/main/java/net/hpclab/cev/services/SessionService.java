package net.hpclab.cev.services;

import java.io.Serializable;
import java.util.HashMap;
import net.hpclab.cev.entities.Users;

public class SessionService implements Serializable {

	private static final long serialVersionUID = -6123381839231323244L;
	private static final HashMap<String, UserSession> ONLINE_USERS = new HashMap<>();
	private static SessionService sessionService;

	private SessionService() {
		super();
	}

	public void addUser(String sessionId, UserSession userSession) {
		ONLINE_USERS.put(sessionId, userSession);
	}

	public void removeUser(String sessionId) {
		ONLINE_USERS.remove(sessionId);
	}

	public boolean isUserOnline(Users users) {
		for (String sessionId : ONLINE_USERS.keySet()) {
			if (ONLINE_USERS.get(sessionId).getUser().equals(users)) {
				return true;
			}
		}
		return false;
	}

	public UserSession getUserSession(String sessionId) {
		return ONLINE_USERS.get(sessionId);
	}

	public synchronized static SessionService getInstance() {
		return sessionService == null ? (sessionService = new SessionService()) : sessionService;
	}
}
