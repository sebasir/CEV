package net.hpclab.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import net.hpclab.entities.AuthorRole;
import net.hpclab.entities.Location;
import net.hpclab.entities.LocationLevel;
import net.hpclab.entities.Specimen;
import net.hpclab.entities.SpecimenContent;
import net.hpclab.entities.Taxonomy;
import net.hpclab.entities.TaxonomyLevel;
import net.hpclab.entities.entNaming;
import org.apache.commons.io.IOUtils;

public class Utilsbean implements Serializable {

    private static HashMap<String, String> ENV_CONF;
    public static List<Specimen> allSpecimens;
    public static List<SpecimenContent> allSpecimenContents;
    public static List<Taxonomy> allTaxonomys;
    public static List<Location> allLocations;
    public static List<TaxonomyLevel> allTaxonomyLevels;
    public static List<LocationLevel> allLocationLevels;
    public static List<AuthorRole> allAuthorRoles;
    
    public static enum Actions {

	   createSuccess, updateSuccess, deleteSuccess, createError, updateError, deleteError, fileRequired, fileUploadSuccess, fileUploadError, fileDeleteError
    };

    public String formatDate(Date date) {
	   return formatDate(date, "dd/MM/yyyy");
    }

    public String formatDate(Date date, String format) {
	   String retorno;
	   try {
		  if (date == null) {
			 retorno = "--";
		  } else {
			 retorno = new SimpleDateFormat(format).format(date);
		  }
	   } catch (Exception e) {
		  e.printStackTrace();
		  retorno = date.toString();
	   }
	   return retorno;
    }

    public FacesMessage showMessage(entNaming ent, Actions act) {
	   FacesMessage fMess;
	   switch (act) {
		  case createSuccess:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_INFO, ent.getEntityName() + " creado.", "Se ha creado el registro " + ent.getDescription() + " satisfactoriamente!");
			 break;
		  case deleteSuccess:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_INFO, ent.getEntityName() + " eliminado.", "Se ha eliminado el registro " + ent.getDescription() + " satisfactoriamente!");
			 break;
		  case updateSuccess:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_INFO, ent.getEntityName() + " actualizado.", "Se ha actualizado el registro " + ent.getDescription() + " satisfactoriamente!");
			 break;
		  case createError:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, ent.getEntityName() + " falló al crear.", "Ha fallado la creación de " + ent.getDescription() + "!");
			 break;
		  case deleteError:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, ent.getEntityName() + " falló al eliminar.", "Ha fallado la eliminación de " + ent.getDescription() + "!");
			 break;
		  case updateError:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, ent.getEntityName() + " falló al actualizar.", "Ha fallado la actualización de " + ent.getDescription() + "!");
			 break;
		  case fileRequired:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Formulario de " + ent.getEntityName() + " Incompleto", "Se necesita un archivo válido");
			 break;
		  default:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_WARN, "ET llama a casa", "Hola. Probando un mensaje no contemplado!!");
			 break;
	   }
	   return fMess;
    }

    public FacesMessage showFileMessage(String fileName, Actions act) {
	   FacesMessage fMess;
	   switch (act) {
		  case fileUploadSuccess:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_INFO, "Archivo adjuntado", "Se ha adjuntado el archivo " + fileName + " satisfactoriamente!");
			 break;
		  case fileUploadError:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Archivo no adjuntado", "Se ha producido un error adjuntando el archivo " + fileName + "!");
			 break;
		  case fileDeleteError:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Archivo no eliminado", "Se ha producido un error eliminando el archivo " + fileName + "!");
			 break;
		  default:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_WARN, "ET llama a casa", "Hola. Probando un mensaje no contemplado!!");
			 break;
	   }
	   return fMess;
    }

    public HashMap<String, String> getENV_CONF() {
	   return ENV_CONF;
    }

    public void setENV_CONF(HashMap<String, String> ENV_CONF) {
	   Utilsbean.ENV_CONF = ENV_CONF;
    }

    public byte[] getByteArray(InputStream in) throws IOException {
	   return IOUtils.toByteArray(in);
    }

    public InputStream getInputStream(byte[] byteContent) throws IOException {
	   return new ByteArrayInputStream(byteContent);
    }
}
