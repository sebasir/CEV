package net.hpclab.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.faces.application.FacesMessage;
import net.hpclab.entities.entNaming;

public class Utilsbean implements Serializable {

    private static HashMap<String, String> ENV_CONF;

    public enum Actions {

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

    public boolean copyFile(String fileName, InputStream in) throws IOException {
	   String path = getENV_CONF().get("PATH_FILES");
	   String pS = getENV_CONF().get("PATH_SEP");
	   OutputStream out = new FileOutputStream(new File(path + pS + fileName));
	   int read;
	   byte[] bytes = new byte[1024];
	   while ((read = in.read(bytes)) != -1) {
		  out.write(bytes, 0, read);
	   }
	   in.close();
	   out.flush();
	   out.close();
	   return true;
    }

    public boolean deleteFile(String fileName) throws IOException {
	   String path = getENV_CONF().get("PATH_FILES");
	   String pS = getENV_CONF().get("PATH_SEP");
	   File delFile = new File(path + pS + fileName);
	   if (delFile.exists()) {
		  return delFile.delete();
	   }
	   return false;
    }

    public FileInputStream getInputStream(String fileName) throws IOException {
	   String path = getENV_CONF().get("PATH_FILES");
	   String pS = getENV_CONF().get("PATH_SEP");
	   File file = new File(path + pS + fileName);
	   return new FileInputStream(file);
    }
}
