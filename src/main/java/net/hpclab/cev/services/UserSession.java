package net.hpclab.cev.services;

import java.io.Serializable;
import net.hpclab.cev.entities.Users;

public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;
    private String ipAddress;
    private Users user;

    public UserSession(Users user, String ipAddress) {
        this.ipAddress = ipAddress;
        this.user = user;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "{User: " + user + ", ipAddress: " + ipAddress + "}";
    }
}
