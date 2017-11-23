package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class WizardBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 2393333682276991006L;
	private static final Logger LOGGER = Logger.getLogger(WizardBean.class.getSimpleName());

	public WizardBean() {
		try {

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
}
