package net.hpclab.sessions;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import net.hpclab.entities.Login;

@Stateless
public class LoginBean extends Generic<Login> {

    public LoginBean() {
        super(Login.class);
    }
    
    public Login autenticarUsuario(String usuario, String contrasenia) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("username", usuario);
        parameters.put("password", contrasenia);
        return super.findSingleByParams("Login.findByIngreso", parameters);
    }

    public Login existeUsuario(String usuario) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("username", usuario);
        return super.findSingleByParams("Login.findByUsername", parameters);
    }
    
    public void editarIntentos(Login login) {
        /*login.setIntentos(login.getIntentos() < 3 ? login.getIntentos() + 1 : login.getIntentos());

        if (login.getIntentos() >= 3) {
            Estado estado = login.getIdEstado();
            estado.setIdEstado(new Long(2));
            login.setIdEstado(estado);
        }*/
        super.merge(login);
    }
}
