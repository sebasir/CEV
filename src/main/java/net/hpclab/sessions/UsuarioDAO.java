package net.hpclab.sessions;

import javax.ejb.Stateless;
import net.hpclab.entities.Usuario;

@Stateless
public class UsuarioDAO extends Generic<Usuario>{
    
    public UsuarioDAO() {
        super(Usuario.class);
    }
}
