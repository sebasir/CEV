package net.hpclab.cev.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import net.hpclab.cev.entities.Taxonomy;

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
