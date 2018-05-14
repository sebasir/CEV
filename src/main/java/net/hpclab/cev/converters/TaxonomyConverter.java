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

package net.hpclab.cev.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import net.hpclab.cev.entities.Taxonomy;

/**
 * Conversor que permite determinar un valor de tipo <tt>Taxonomy</tt> desde un
 * valor de <tt>String</tt> que se selecciona desde una lista en la pantalla
 * 
 * @author Sebasir
 * @since 1.0
 * @see Taxonomy
 */
@FacesConverter("TaxonomyConverter")
public class TaxonomyConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			return new Taxonomy(Integer.parseInt(value));
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
			return String.valueOf(((Taxonomy) object).getIdTaxonomy());
		} else {
			return null;
		}
	}
}
