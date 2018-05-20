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

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import net.hpclab.cev.services.MessagesService;

/**
 * Este servicio permite la interacción con los módulos del sistema,
 * específicamente con las páginas que representan a cada módulo. Principalmente
 * expone métodos de setNavigablePage y getMessage para obtener la URL de la
 * página de la opción seleccionada, o bien, los mensajes de texto que las
 * componen
 * 
 * @author Sebasir
 * @since 1.0
 * @see MessagesService
 */

@Named(value = "navigationBean")
@SessionScoped
public class NavigationBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 2801082271772326617L;

	/**
	 * Página de navegación según se selecciona
	 */
	private String navigablePage;

	/**
	 * Constructor original
	 */
	public NavigationBean() {
	}

	/**
	 * @return Determina si una página está disponible y accesible
	 */
	public boolean isPageReady() {
		return navigablePage != null && !navigablePage.isEmpty();
	}

	/**
	 * @return Página de navegación según se selecciona
	 */
	public String getNavigablePage() {
		return navigablePage;
	}

	/**
	 * @param navigablePage
	 *            Página de navegación según se selecciona a definir
	 */
	public void setNavigablePage(String navigablePage) {
		this.navigablePage = navigablePage;
	}

	/**
	 * Permite obtener un mensaje a partir de una llave definida en una vista
	 * 
	 * @param idMessage
	 *            Clave del mensaje
	 * @return Valor del mensaje
	 */
	public String getMessage(String idMessage) {
		return MessagesService.getInstance().getMessage(idMessage);
	}
}
