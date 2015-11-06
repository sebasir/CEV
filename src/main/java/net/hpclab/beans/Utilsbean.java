package net.hpclab.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.application.FacesMessage;
import net.hpclab.entities.entNaming;

public class Utilsbean implements Serializable {
    public enum Actions {
	   createSuccess, updateSuccess, deleteSuccess, createError, updateError, deleteError
    };

    public String formatDate(Date date) {
	   String retorno, format = "dd/MM/yyyy";
	   try {
		  retorno = new SimpleDateFormat(format).format(date);
	   } catch (Exception e) {

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
		  default:
			 fMess = new FacesMessage(FacesMessage.SEVERITY_WARN, "ET llama a casa", "Hola. Probando un mensaje no contemplado!!");
			 break;
	   }
	   return fMess;
    }
}
