package net.hpclab.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import net.hpclab.entities.Usuario;
import net.hpclab.sessions.UsuarioDAO;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;

@ViewScoped
@ManagedBean
public class UsuarioController implements Serializable {

    private static final Logger logger = Logger.getLogger(UsuarioController.class);
    private @Inject
    UsuarioDAO injectUsuario;
    private List<Usuario> filtrarUsuarios;
    private LazyDataModel<Usuario> lazyModel;
    private Usuario selectedUsuario;
    private final HttpSession session;

    public UsuarioController() {
	   session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    }

    @PostConstruct
    public void init() {
	   logger.debug("--> UsuarioController inicializo");
    }

    public void onRowSelect(SelectEvent event) {
	   messageUtil.addMessage("Usuario Seleccionado ", ((Usuario) event.getObject()).getNombre());
	   selectedUsuario = ((Usuario) event.getObject());
    }

    public void onRowUnselect(UnselectEvent event) {
	   messageUtil.addMessage("Sin Seleccionar", ((Usuario) event.getObject()).getNombre());
    }

    public void doCrearUsuario(ActionEvent event) {
	   logger.error("--> Entro al metodo doCrearUsuario");
	   selectedUsuario.setFechaCreacion(new Date());
	   injectUsuario.persist(selectedUsuario);
	   messageUtil.addSuccessMessage("Se creo el usuario " + selectedUsuario.getNombre());
    }

    public void doActualizarUsuario(ActionEvent event) {
	   logger.debug("--> Entro al metodo doActualizarUsuario");
	   injectUsuario.merge(selectedUsuario);
	   messageUtil.addSuccessMessage("Se edito el usuario " + selectedUsuario.getNombre());
    }

    public void doInactivarUsuario(ActionEvent event) {
	   logger.debug("--> Entro al metodo doInactivarUsuario");
	   injectUsuario.merge(selectedUsuario);
	   messageUtil.addSuccessMessage("Se elimino el producto " + selectedUsuario.getNombre());
    }

    public void prepareCreate(ActionEvent event) {
	   logger.debug("--> Entro al metodo prepareCreate Usuario");
	   if (this.selectedUsuario != null) {
		  logger.debug("--> Entro al metodo prepareCreate Usuario  " + this.selectedUsuario.getCedula());
	   }

	   this.selectedUsuario = new Usuario();
    }

    public Usuario getSelectedUsuario() {
	   return selectedUsuario;
    }

    public void setSelectedUsuario(Usuario selectedUsuario) {
	   this.selectedUsuario = selectedUsuario;
    }

    public List<Usuario> getFiltrarUsuarios() {
	   return filtrarUsuarios;
    }

    public void setFiltrarUsuarios(List<Usuario> filtrarUsuarios) {
	   this.filtrarUsuarios = filtrarUsuarios;
    }

    public LazyDataModel<Usuario> getLazyModel() {
	   return lazyModel;
    }

    public boolean isValidationFailed() {
	   return messageUtil.isValidationFailed();
    }
}
