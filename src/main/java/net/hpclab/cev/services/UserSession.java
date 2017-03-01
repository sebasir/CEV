package net.hpclab.cev.services;

import java.io.Serializable;
import javax.servlet.http.HttpSession;
import net.hpclab.cev.entities.Users;

public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;
    public static HttpSession session;
    public static String ipAddress;
    public static Users user;

}
