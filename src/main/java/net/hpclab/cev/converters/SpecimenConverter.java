package net.hpclab.cev.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import net.hpclab.cev.entities.Specimen;

@FacesConverter("SpecimenConverter")
public class SpecimenConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		try {
			if (value != null && value.trim().length() > 0) {
				return new Specimen(Integer.parseInt(value));
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
			return String.valueOf(((Specimen) object).getIdSpecimen());
		} else {
			return null;
		}
	}
}
