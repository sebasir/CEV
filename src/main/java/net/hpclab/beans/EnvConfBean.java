package net.hpclab.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.EnvConf;
import net.hpclab.sessions.EnvConfSession;

@ManagedBean(eager = true)
@ApplicationScoped
public class EnvConfBean extends Utilsbean implements Serializable {

    @Inject
    private EnvConfSession envConfSession;

    private static final long serialVersionUID = 1L;
    private EnvConf envConf;
    private List<EnvConf> allEnvConfs;

    public EnvConfBean() {
	   envConfSession = new EnvConfSession();
    }

    @PostConstruct
    public void init() {
	   findAllEnvConfs();
    }

    public void persist() {
	   try {
		  setEnvConf(envConfSession.persist(getEnvConf()));
		  if (getEnvConf() != null && getEnvConf().getIdConfig() != null) {
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(envConf, Actions.createSuccess));
		  } else {
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(envConf, Actions.createError));
		  }
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(envConf, Actions.createError));
	   }
    }

    public void delete() {
	   try {
		  envConfSession.delete(getEnvConf());
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getEnvConf(), Actions.deleteSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getEnvConf(), Actions.deleteError));
	   }
    }

    public void prepareCreate() {
	   setEnvConf(new EnvConf());
    }

    public void edit() {
	   try {
		  setEnvConf(envConfSession.merge(getEnvConf()));
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getEnvConf(), Actions.updateSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getEnvConf(), Actions.updateError));
	   }
    }

    public void findAllEnvConfs() {
	   setAllEnvConfs(envConfSession.listAll());
	   HashMap<String, String> confs = new HashMap<String, String>();
	   for (EnvConf e : allEnvConfs) {
		  confs.put(e.getConfigName(), e.getConfigValue());
	   }
	   super.setENV_CONF(confs);
    }

    public EnvConf getEnvConf() {
	   return envConf;
    }

    public void setEnvConf(EnvConf envConf) {
	   this.envConf = envConf;
    }

    public List<EnvConf> getAllEnvConfs() {
	   findAllEnvConfs();
	   return allEnvConfs;
    }

    public void setAllEnvConfs(List<EnvConf> allEnvConfs) {
	   this.allEnvConfs = allEnvConfs;
    }
}
