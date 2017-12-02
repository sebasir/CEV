package net.hpclab.cev.services;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Properties;
import net.hpclab.cev.enums.AuthenticateEnum;

public class MessagesService implements Serializable {

	private static final long serialVersionUID = 1L;
	private static MessagesService messagesService;
	private static Properties messages;

	public void loadMessages(Properties messages) {
		MessagesService.messages = messages;
	}

	public String getMessage(AuthenticateEnum ae, Object... params) {
		return getMessage(ae.name(), params);
	}

	public String getMessage(String prop, Object... params) {
		String message = messages.getProperty(prop);
		if (params != null) {
			message = MessageFormat.format(message, params);
		}
		return message;
	}

	public static synchronized MessagesService getInstance() {
		return messagesService == null ? (messagesService = new MessagesService()) : messagesService;
	}
}
