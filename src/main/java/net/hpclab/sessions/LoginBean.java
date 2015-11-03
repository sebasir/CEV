package net.hpclab.sessions;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import net.hpclab.entities.Login;
import org.apache.log4j.Logger;

@Stateless
public class LoginBean extends Generic<Login> {

    private static final Logger logger = Logger.getLogger(LoginBean.class);

    public LoginBean() {
        super(Login.class);
    }
    
    public Login autenticarUsuario(String usuario, String contrasenia) {
        logger.debug("--> Entro al metodo autenticarUsuario");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("username", usuario);
        parameters.put("password", contrasenia);
        return super.findByParams("Login.findByIngreso", parameters);
    }

    public Login existeUsuario(String usuario) {
        logger.debug("--> Entro al metodo existeUsuario");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("username", usuario);
        return super.findByParams("Login.findByUsername", parameters);
    }
    
    public void editarIntentos(Login login) {
        logger.debug("--> Entro al metodo editarIntentos");
        /*login.setIntentos(login.getIntentos() < 3 ? login.getIntentos() + 1 : login.getIntentos());

        if (login.getIntentos() >= 3) {
            Estado estado = login.getIdEstado();
            estado.setIdEstado(new Long(2));
            login.setIdEstado(estado);
        }*/
        super.merge(login);
    }
}
