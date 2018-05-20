/*
 * Colección Entomológica Virtual
 * Universidad Central
 * High Performance Computing Laboratory
 * Grupo COMMONS.
 * 
 * Sebastián Motavita Medellín
 * 
 * 2017 - 2018
 */

package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Este servicio no se ha implementado aun
 * 
 * @author Sebasir
 * @since 1.0
 */

@ManagedBean
@ViewScoped
public class WizardBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 2393333682276991006L;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(WizardBean.class.getSimpleName());

	/**
	 * Constructor del servicio
	 */
	public WizardBean() {
		try {

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
}
